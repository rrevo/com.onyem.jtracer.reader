package com.onyem.jtracer.reader.meta.internal.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.onyem.jtracer.reader.db.util.Constants;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.internal.ClassFactory;
import com.onyem.jtracer.reader.meta.internal.ClassNameUtils;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;
import com.onyem.jtracer.reader.meta.internal.MetaService;
import com.onyem.jtracer.reader.meta.internal.MethodImpl;
import com.onyem.jtracer.reader.meta.internal.TypeConstants;

@Immutable
public class MetaParserHelper {

  private final String INDEX_CLASS = "ic"; // Class index
  private final String INDEX_METHOD = "im"; // Method index
  private final ClassNameUtils nameUtils;

  @Inject
  MetaParserHelper(ClassNameUtils nameUtils) {
    this.nameUtils = nameUtils;
  }

  public IClass getClassFromLine(IMetaServiceExtended metaService,
      String classLine) {
    if (classLine == null) {
      return null;
    }
    String[] parts = getLineParts(classLine);

    String eventName = parts[0];
    Long metaId = Long.parseLong(parts[1]);
    // classVersion = Integer.parseInt(parts[2]);

    if (eventName.equals(INDEX_CLASS)) {
      int access = Integer.parseInt(parts[3]);
      String className = parts[4];
      String signature = parts[5];
      if (signature != null && signature.trim().isEmpty()) {
        signature = null;
      }
      String superClassName = parts[6].trim();
      IClass superClass = null;
      if (!superClassName.isEmpty()) {
        superClass = metaService.getClassByCanonicalName(nameUtils
            .getCanonicalClassName(superClassName));
      }
      Set<IClass> interfaces = new HashSet<IClass>();
      if (parts.length > 7) {
        String interfaceData = parts[7];
        String[] interfaceClassNames = interfaceData.split(",");
        for (String interfaceClassName : interfaceClassNames) {
          interfaceClassName = interfaceClassName.trim();
          if (interfaceClassName.length() > 0) {
            interfaces.add(metaService.getInterfaceByCanonicalName(nameUtils
                .getCanonicalClassName(interfaceClassName)));
          }
        }
      }

      String classNameRaw = nameUtils.getCanonicalClassName(className);
      className = nameUtils.getClassFromCanonicalName(classNameRaw);
      String packageName = nameUtils.getPackageFromCanonicalName(classNameRaw);

      IClass clazz = ClassFactory.createClassOrInterface(metaId, access,
          classNameRaw, className, packageName, signature, superClass,
          interfaces, nameUtils);
      return clazz;
    } else {
      throw new RuntimeException();
    }
  }

  private String[] getLineParts(String data) {
    if (data.charAt(0) == '#') {
      return null;
    }
    // Remove the first and last char
    data = data.substring(1, data.length() - 1);
    String[] parts = data.split("\\|");
    return parts;
  }

  public IClass getClassFromCanonicalName(String canonicalName,
      boolean isInterface) {
    String className = nameUtils.getClassFromCanonicalName(canonicalName);
    String packageName = nameUtils.getPackageFromCanonicalName(canonicalName);

    if (!isInterface) {
      IClass clazz = ClassFactory.createClass(canonicalName, className,
          packageName);
      return clazz;
    } else {
      IClass clazz = ClassFactory.createInterface(canonicalName, className,
          packageName);
      return clazz;
    }
  }

  public IClass getClassFromComponentType(String canonicalName,
      IClass componentType) {
    IClass clazz = ClassFactory.createArray(canonicalName, componentType);
    return clazz;
  }

  public IMethod getMethodFromLine(IMetaServiceExtended metaService,
      String methodLine) {
    if (methodLine == null) {
      return null;
    }
    String[] parts = getLineParts(methodLine);

    String eventName = parts[0];
    Long metaId = Long.parseLong(parts[1]);

    if (eventName.equals(INDEX_METHOD)) {
      Long classIndex = Long.parseLong(parts[2]);
      IClass clazz = metaService.getClassByMetaId(classIndex);

      int access = Integer.parseInt(parts[3]);
      String name = parts[4];
      String description = parts[5];
      String signature = parts.length > 6 ? parts[6] : null;
      if (signature != null && signature.trim().isEmpty()) {
        signature = null;
      }

      IClass returnType = getReturnType(metaService, description);
      List<IClass> parameters = getParameters(metaService, description);

      List<IClass> exceptions = new ArrayList<IClass>();
      if (parts.length > 7) {
        String exceptionData = parts[7];
        String[] exceptionClasses = exceptionData.split(",");
        for (String exceptionClass : exceptionClasses) {
          exceptionClass = exceptionClass.trim();
          if (exceptionClass.length() > 0) {
            exceptions.add(metaService.getClassByCanonicalName(nameUtils
                .getCanonicalClassName(exceptionClass)));
          }
        }
      }

      return MethodImpl.createMethod(Constants.NULL_ID, metaId, access, name,
          clazz, parameters, returnType, exceptions, description, signature);

    } else {
      throw new RuntimeException();
    }
  }

  public IClass getReturnType(IMetaServiceExtended metaService,
      String description) {
    int returnIndex = description.indexOf(")");
    String returnTypeName = description.substring(returnIndex + 1);
    return metaService.getClassByCanonicalName(returnTypeName);
  }

  public List<IClass> getParameters(IMetaServiceExtended metaService,
      String description) {
    int returnIndex = description.indexOf(")");
    String classNamesStream = description.substring(1, returnIndex);
    return getClassesFromNameStream(metaService, classNamesStream);
  }

  private List<IClass> getClassesFromNameStream(
      IMetaServiceExtended metaService, String classNamesStream) {
    if (classNamesStream.isEmpty()) {
      return Collections.emptyList();
    }
    List<IClass> clazzes = new ArrayList<IClass>();
    while (!classNamesStream.isEmpty()) {
      int firstSize = getFirstClassNameLength(classNamesStream);
      String classNameRaw = classNamesStream.substring(0, firstSize);
      IClass invocationClass = metaService
          .getClassByCanonicalName(classNameRaw);
      clazzes.add(invocationClass);
      classNamesStream = classNamesStream.substring(firstSize);
    }
    clazzes = Collections.unmodifiableList(clazzes);
    return clazzes;
  }

  private int getFirstClassNameLength(String classNameRawStream) {
    if (classNameRawStream.startsWith(TypeConstants.CLASS_PREFIX)) {
      int index = classNameRawStream.indexOf(TypeConstants.CLASS_SUFFIX);
      return index + 1;
    } else if (classNameRawStream.startsWith(TypeConstants.ARRAY_PREFIX)) {
      return 1 + getFirstClassNameLength(classNameRawStream.substring(1));
    } else if (classNameRawStream.startsWith(TypeConstants.BYTE)
        || classNameRawStream.startsWith(TypeConstants.CHAR)
        || classNameRawStream.startsWith(TypeConstants.DOUBLE)
        || classNameRawStream.startsWith(TypeConstants.FLOAT)
        || classNameRawStream.startsWith(TypeConstants.INT)
        || classNameRawStream.startsWith(TypeConstants.LONG)
        || classNameRawStream.startsWith(TypeConstants.SHORT)
        || classNameRawStream.startsWith(TypeConstants.BOOLEAN)
        || classNameRawStream.startsWith(TypeConstants.VOID)) {
      return 1;
    } else {
      throw new RuntimeException();
    }
  }

  public IMethod getMethodFromName(MetaService metaService, String name,
      String description, IClass returnType, List<IClass> parameters,
      IClass clazz) {
    return MethodImpl.createMethod(Constants.NULL_ID, null, null, name, clazz,
        parameters, returnType, new ArrayList<IClass>(), description, null);
  }
}
