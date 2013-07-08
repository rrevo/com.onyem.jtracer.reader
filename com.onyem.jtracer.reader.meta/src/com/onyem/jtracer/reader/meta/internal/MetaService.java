package com.onyem.jtracer.reader.meta.internal;

import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.internal.dao.ClassDAO;
import com.onyem.jtracer.reader.meta.internal.dao.MethodDAO;
import com.onyem.jtracer.reader.meta.internal.parser.MetaParserHelper;
import com.onyem.jtracer.reader.parser.IMetaParser;

@Service
@ThreadSafe
public class MetaService implements IMetaServiceExtended {

  private final IMetaParser metaParser;
  private final MetaParserHelper metaParserHelper;
  private final ClassNameUtils nameUtils;

  private ClassDAO classDAO;
  private MethodDAO methodDAO;

  MetaService(ClassNameUtils nameUtils, MetaParserHelper metaParserHelper,
      @Assisted IMetaParser metaParser) {
    this.metaParser = metaParser;
    this.metaParserHelper = metaParserHelper;
    this.nameUtils = nameUtils;
  }

  synchronized void setClassDAO(ClassDAO classDAO) {
    this.classDAO = classDAO;
  }

  synchronized void setMethodDAO(MethodDAO methodDAO) {
    this.methodDAO = methodDAO;
  }

  @Override
  public synchronized void close() throws IOException {
    metaParser.close();
  }

  @Override
  public synchronized IClass getClassById(long id) {
    IClass clazz = classDAO.getClassById(id);
    return clazz;
  }

  @Override
  public synchronized ClassId getClassIdById(long id) {
    return classDAO.getClassIdById(id);
  }

  @Override
  public synchronized IClass getClassByMetaId(long metaId) {
    IClass clazz = classDAO.getClassByMetaId(metaId);
    if (clazz != null) {
      return clazz;
    } else {
      String classLine = metaParser.getLine(metaId);
      IClass classFromLine = metaParserHelper.getClassFromLine(this, classLine);
      clazz = classDAO.insertClass(classFromLine);
    }
    return clazz;
  }

  @Override
  public synchronized IClass getPlainClassByName(String name) {
    name = nameUtils.getCanonicalClassName(name);
    return getClassByCanonicalName(name);
  }

  @Override
  public synchronized IClass getClassByCanonicalName(String canonicalName) {
    if (nameUtils.isVoid(canonicalName, true)) {
      String name = nameUtils.getVoidName(canonicalName);
      return classDAO.getPrimitiveByName(name);
    }
    if (nameUtils.isPrimitive(canonicalName, true)) {
      String name = nameUtils.getPrimitiveName(canonicalName);
      return classDAO.getPrimitiveByName(name);
    }
    if (nameUtils.isPlainClass(canonicalName)) {
      String className = nameUtils.getClassFromCanonicalName(canonicalName);
      String packageName = nameUtils.getPackageFromCanonicalName(canonicalName);
      IClass clazz = classDAO.getClassByName(packageName, className, false);
      if (clazz != null) {
        return clazz;
      } else {
        IClass clazzFromName = metaParserHelper.getClassFromCanonicalName(
            canonicalName, false);
        return classDAO.insertClass(clazzFromName);
      }
    }
    if (nameUtils.isArray(canonicalName)) {
      String componentTypeName = nameUtils.getArrayComponentType(canonicalName);
      IClass componentType = getClassByCanonicalName(componentTypeName);
      IClass clazz = classDAO.getArrayByComponentType(componentType);
      if (clazz != null) {
        return clazz;
      } else {
        IClass clazzFromName = metaParserHelper.getClassFromComponentType(
            canonicalName, componentType);
        return classDAO.insertClass(clazzFromName);
      }
    }
    throw new IllegalArgumentException();
  }

  @Override
  public synchronized IClass getInterfaceByCanonicalName(String canonicalName) {
    String className = nameUtils.getClassFromCanonicalName(canonicalName);
    String packageName = nameUtils.getPackageFromCanonicalName(canonicalName);
    IClass clazz = classDAO.getClassByName(packageName, className, true);
    if (clazz != null) {
      return clazz;
    } else {
      IClass clazzFromName = metaParserHelper.getClassFromCanonicalName(
          canonicalName, true);
      return classDAO.insertClass(clazzFromName);
    }
  }

  @Override
  public synchronized IMethod getMethodByMetaId(long metaId) {
    IMethod method = methodDAO.getMethodByMetaId(metaId);
    if (method != null) {
      return method;
    } else {
      String methodLine = metaParser.getLine(metaId);
      IMethod methodFromName = metaParserHelper.getMethodFromLine(this,
          methodLine);
      return methodDAO.insertMethod(methodFromName);
    }
  }

  @Override
  public synchronized IMethod getMethodById(long id) {
    IMethod method = methodDAO.getMethodById(id);
    return method;
  }

  @Override
  public synchronized IClass getMethodClass(IMethod method) {
    ClassId classId = method.getIClass();
    return getClassById(classId.getId());
  }

  /*
   * For traced classes, the exception throw will be followed only after method
   * entry events. So the traced classes should be found in the db
   * For untraced classes, partial method data should be inserted into the db
   */
  @Override
  public synchronized IMethod getMethodByNameDescription(String name,
      String description, ClassId clazz) {
    // Validate the description and insert the classes if necessary
    IClass returnType = metaParserHelper.getReturnType(this, description);
    List<IClass> parameters = metaParserHelper.getParameters(this, description);
    IMethod method = methodDAO.getMethodByName(clazz, name, description);
    if (method != null) {
      return method;
    } else {
      IMethod methodFromName = metaParserHelper.getMethodFromName(this, name,
          description, returnType.getId(), ClassUtils.getClassIds(parameters),
          clazz);
      return methodDAO.insertMethod(methodFromName);
    }
  }
}
