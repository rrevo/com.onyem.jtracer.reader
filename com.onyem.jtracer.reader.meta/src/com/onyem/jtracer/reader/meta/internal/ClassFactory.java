package com.onyem.jtracer.reader.meta.internal;

import java.util.Set;

import com.onyem.jtracer.reader.db.util.Constants;
import com.onyem.jtracer.reader.meta.ClassType;
import com.onyem.jtracer.reader.meta.IClass;

public class ClassFactory {

  public static IClass createClassOrInterface(Long metaId, Integer access,
      String canonicalName, String className, String packageName,
      String signature, IClass superClass, Set<IClass> interfaces,
      ClassNameUtils nameUtils) {

    if (access != null) {
      boolean isInterface = (access & 0x0200) > 0;
      ClassType classType = isInterface ? ClassType.INTERFACE : ClassType.CLASS;

      return ClassImpl.create(Constants.NULL_ID, metaId, access, className,
          packageName, classType, superClass, interfaces, null, signature,
          canonicalName);
    } else {
      return ClassImpl.create(Constants.NULL_ID, metaId, access, className,
          packageName, ClassType.CLASS, superClass, interfaces, null,
          signature, canonicalName);
    }
  }

  public static IClass createClass(String canonicalName, String className,
      String packageName) {
    return ClassImpl.create(Constants.NULL_ID, null, null, className,
        packageName, ClassType.CLASS, null, null, null, null, canonicalName);
  }

  public static IClass createInterface(String canonicalName, String className,
      String packageName) {
    return ClassImpl
        .create(Constants.NULL_ID, null, null, className, packageName,
            ClassType.INTERFACE, null, null, null, null, canonicalName);
  }

  public static IClass createArray(String canonicalName, IClass componentType) {
    return ClassImpl.create(Constants.NULL_ID, null, null, null, null,
        ClassType.ARRAY, null, null, componentType, null, canonicalName);
  }

}
