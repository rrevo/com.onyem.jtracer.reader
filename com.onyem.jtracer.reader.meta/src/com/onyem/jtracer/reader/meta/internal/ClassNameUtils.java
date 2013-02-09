package com.onyem.jtracer.reader.meta.internal;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.onyem.jtracer.reader.meta.IClass;

@Immutable
public class ClassNameUtils {

  @Inject
  ClassNameUtils() {
  }

  /*
   * org.world.HelloWorld returns Lorg/world/HelloWorld;
   * org/world/HelloWorld returns Lorg/world/HelloWorld;
   */
  public String getCanonicalClassName(String name) {
    name = name.replace(".", "/");
    return TypeConstants.CLASS_PREFIX + name + TypeConstants.CLASS_SUFFIX;
  }

  /*
   * null, HelloWorld returns LHelloWorld;
   * org.world, HelloWorld returns Lorg/world/HelloWorld;
   */
  public String getCanonicalClassName(String packageName, String className) {
    if (packageName == null) {
      return getCanonicalClassName(className);
    } else {
      return getCanonicalClassName(packageName + "." + className);
    }
  }

  /*
   * Lorg/world/HelloWorld; returns HelloWorld
   * LFoo; returns Foo
   */
  public String getClassFromCanonicalName(String canonicalClassName) {
    String className = canonicalClassName.substring(1,
        canonicalClassName.length() - 1);
    int index = className.lastIndexOf("/");
    if (index != -1) {
      className = className.substring(index + 1);
    }
    return className;
  }

  public String getCanonicalClassName(boolean isClass, boolean isInterface,
      boolean isPrimitive, boolean isVoid, boolean isArray, String className,
      String packageName, IClass componentClazz) {
    if (isClass || isInterface) {
      return getCanonicalClassName(packageName, className);
    }
    if (isPrimitive) {
      if (className.equals(TypeConstants.BYTE_NAME)) {
        return TypeConstants.BYTE;
      } else if (className.equals(TypeConstants.CHAR_NAME)) {
        return TypeConstants.CHAR;
      } else if (className.equals(TypeConstants.DOUBLE_NAME)) {
        return TypeConstants.DOUBLE;
      } else if (className.equals(TypeConstants.FLOAT_NAME)) {
        return TypeConstants.FLOAT;
      } else if (className.equals(TypeConstants.INT_NAME)) {
        return TypeConstants.INT;
      } else if (className.equals(TypeConstants.LONG_NAME)) {
        return TypeConstants.LONG;
      } else if (className.equals(TypeConstants.SHORT_NAME)) {
        return TypeConstants.SHORT;
      } else if (className.equals(TypeConstants.BOOLEAN_NAME)) {
        return TypeConstants.BOOLEAN;
      } else {
        throw new IllegalArgumentException();
      }
    }
    if (isVoid) {
      return TypeConstants.VOID;
    }
    if (isArray) {
      return TypeConstants.ARRAY_PREFIX + componentClazz.getCanonicalName();
    }
    throw new IllegalArgumentException();
  }

  /*
   * Lorg/world/HelloWorld; returns org.world
   * LFoo; returns null
   */
  public String getPackageFromCanonicalName(String canonicalClassName) {
    String className = canonicalClassName.substring(1,
        canonicalClassName.length() - 1);
    int index = className.lastIndexOf("/");
    String packageName = null;
    if (index != -1) {
      packageName = className.substring(0, index);
      packageName = packageName.replace("/", ".");
    }
    return packageName;
  }

  public boolean isPrimitive(String name, boolean canonical) {
    if (canonical) {
      if (name.equals(TypeConstants.BYTE) || name.equals(TypeConstants.CHAR)
          || name.equals(TypeConstants.DOUBLE)
          || name.equals(TypeConstants.FLOAT) || name.equals(TypeConstants.INT)
          || name.equals(TypeConstants.LONG)
          || name.equals(TypeConstants.SHORT)
          || name.equals(TypeConstants.BOOLEAN)) {
        return true;
      }
    } else {
      if (name.equals(TypeConstants.BYTE_NAME)
          || name.equals(TypeConstants.CHAR_NAME)
          || name.equals(TypeConstants.DOUBLE_NAME)
          || name.equals(TypeConstants.FLOAT_NAME)
          || name.equals(TypeConstants.INT_NAME)
          || name.equals(TypeConstants.LONG_NAME)
          || name.equals(TypeConstants.SHORT_NAME)
          || name.equals(TypeConstants.BOOLEAN_NAME)) {
        return true;
      }
    }
    return false;
  }

  public boolean isVoid(String name, boolean canonical) {
    if (canonical) {
      if (name.equals(TypeConstants.VOID)) {
        return true;
      }
    } else {
      if (name.equals(TypeConstants.VOID_NAME)) {
        return true;
      }
    }
    return false;

  }

  public boolean isArray(String canonicalClassName) {
    return canonicalClassName.startsWith(TypeConstants.ARRAY_PREFIX);
  }

  public boolean isPlainClass(String canonicalClassName) {
    return canonicalClassName.startsWith(TypeConstants.CLASS_PREFIX)
        && canonicalClassName.endsWith(TypeConstants.CLASS_SUFFIX);
  }

  public String getArrayComponentType(String canonicalClassName) {
    assert TypeConstants.ARRAY_PREFIX
        .equals(canonicalClassName.substring(0, 1));

    return canonicalClassName.substring(1);
  }

  public String getPrimitiveCanonicalName(String name) {
    return getCanonicalClassName(false, false, true, false, false, name, null,
        null);
  }

  public String getPrimitiveName(String canonicalName) {
    if (canonicalName.equals(TypeConstants.BYTE)) {
      return TypeConstants.BYTE_NAME;
    } else if (canonicalName.equals(TypeConstants.CHAR)) {
      return TypeConstants.CHAR_NAME;
    } else if (canonicalName.equals(TypeConstants.DOUBLE)) {
      return TypeConstants.DOUBLE_NAME;
    } else if (canonicalName.equals(TypeConstants.FLOAT)) {
      return TypeConstants.FLOAT_NAME;
    } else if (canonicalName.equals(TypeConstants.INT)) {
      return TypeConstants.INT_NAME;
    } else if (canonicalName.equals(TypeConstants.LONG)) {
      return TypeConstants.LONG_NAME;
    } else if (canonicalName.equals(TypeConstants.SHORT)) {
      return TypeConstants.SHORT_NAME;
    } else if (canonicalName.equals(TypeConstants.BOOLEAN)) {
      return TypeConstants.BOOLEAN_NAME;
    } else {
      throw new IllegalArgumentException();
    }
  }

  public String getVoidName(String canonicalName) {
    if (canonicalName.equals(TypeConstants.VOID)) {
      return TypeConstants.VOID_NAME;
    } else {
      throw new IllegalArgumentException();
    }
  }

}
