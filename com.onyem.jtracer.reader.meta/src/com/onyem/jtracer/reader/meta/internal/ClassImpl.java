package com.onyem.jtracer.reader.meta.internal;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.ClassType;
import com.onyem.jtracer.reader.meta.IClass;

@Immutable
public class ClassImpl implements IClass {

  public static IClass create(long id, Long metaId, Integer access,
      String className, String packageName, ClassType classType,
      ClassId superClazz, Set<ClassId> interfaces, IClass componentClazz,
      String signature, String canonicalName) {
    return new ClassImpl(id, metaId, access, classType, className, packageName,
        superClazz, interfaces, componentClazz, signature, canonicalName);
  }

  private final ClassId id;
  private final Long metaId;
  private final Integer access;
  private final ClassType classType;
  private final String className;
  private final String packageName;
  private final ClassId superClass;
  private final Set<ClassId> interfaces;
  private final ClassId componentType;
  private final String signature;
  private final String canonicalName;

  private final transient String arraySimpleName;
  private final transient String arrayCompleteName;

  private ClassImpl(long id, Long metaId, Integer access, ClassType classType,
      String className, String packageName, ClassId superClass,
      Set<ClassId> interfaces, IClass componentClazz, String signature,
      String canonicalName) {
    this.id = new ClassIdImpl(id);
    this.metaId = metaId;
    this.access = access;
    this.classType = classType;
    this.className = className;
    this.packageName = packageName;

    if (componentClazz == null) {
      this.componentType = null;
      arraySimpleName = null;
      arrayCompleteName = null;
    } else {
      this.componentType = componentClazz.getId();
      arraySimpleName = componentClazz.getSimpleName() + "[]";
      arrayCompleteName = componentClazz.getCompleteName() + "[]";
    }
    this.superClass = superClass;
    this.interfaces = initializeInterfaces(interfaces);

    this.signature = signature;
    this.canonicalName = canonicalName;
    validate();
  }

  private void validate() {
    switch (classType) {
    case CLASS:
      assert getComponentType() == null;
      break;
    case INTERFACE:
      assert getComponentType() == null;
      break;
    case PRIMITIVE:
      assert getClassName() == null;
      assert getPackageName() == null;
      assert getSuperClass() == null;
      assert getInterfaces() == null;
      assert getComponentType() == null;
      assert getCanonicalSignature() == null;

      if (getCanonicalName().equals(TypeConstants.BYTE)) {
        assert getSimpleName().equals(TypeConstants.BYTE_NAME);
      } else if (getCanonicalName().equals(TypeConstants.CHAR)) {
        assert getSimpleName().equals(TypeConstants.CHAR_NAME);
      } else if (getCanonicalName().equals(TypeConstants.DOUBLE)) {
        assert getSimpleName().equals(TypeConstants.DOUBLE_NAME);
      } else if (getCanonicalName().equals(TypeConstants.FLOAT)) {
        assert getSimpleName().equals(TypeConstants.FLOAT_NAME);
      } else if (getCanonicalName().equals(TypeConstants.INT)) {
        assert getSimpleName().equals(TypeConstants.INT_NAME);
      } else if (getCanonicalName().equals(TypeConstants.LONG)) {
        assert getSimpleName().equals(TypeConstants.LONG_NAME);
      } else if (getCanonicalName().equals(TypeConstants.SHORT)) {
        assert getSimpleName().equals(TypeConstants.SHORT_NAME);
      } else if (getCanonicalName().equals(TypeConstants.BOOLEAN)) {
        assert getSimpleName().equals(TypeConstants.BOOLEAN_NAME);
      } else {
        throw new AssertionError();
      }
      break;
    case VOID:
      assert getClassName() == null;
      assert getPackageName() == null;
      assert getSuperClass() == null;
      assert getInterfaces() == null;
      assert getComponentType() == null;
      assert getCanonicalSignature() == null;

      assert getSimpleName().equals(TypeConstants.VOID_NAME);
      assert getCanonicalName().equals(TypeConstants.VOID);
      break;
    case ARRAY:
      assert getClassName() == null;
      assert getPackageName() == null;
      assert getCanonicalSignature() == null;
      assert getSuperClass() == null;
      assert getInterfaces() == null;
      break;
    default:
      throw new IllegalStateException();
    }
    assert getSimpleName() != null && !getSimpleName().isEmpty();
    assert getCompleteName() != null && !getCompleteName().isEmpty();
    assert getCanonicalName() != null && !getCanonicalName().isEmpty();
  }

  private Set<ClassId> initializeInterfaces(Set<ClassId> interfaces) {
    if (interfaces == null) {
      interfaces = Collections.emptySet();
    }
    return interfaces;
  }

  @Override
  public ClassId getId() {
    return id;
  }

  @Override
  public Long getMetaId() {
    return metaId;
  }

  @Override
  public Integer getAccess() {
    return access;
  }

  @Override
  public ClassType getClassType() {
    return classType;
  }

  @Nullable
  @Override
  public String getPackageName() {
    return packageName;
  }

  @Override
  @Nullable
  public String getClassName() {
    switch (classType) {
    case CLASS:
    case INTERFACE:
      return className;
    case PRIMITIVE:
    case VOID:
    case ARRAY:
      return null;
    }
    throw new IllegalStateException();
  }

  @Override
  @Nullable
  public ClassId getSuperClass() {
    return superClass;
  }

  @Override
  @Nullable
  public Set<ClassId> getInterfaces() {
    switch (classType) {
    case CLASS:
    case INTERFACE:
      return interfaces;
    case PRIMITIVE:
    case VOID:
    case ARRAY:
      return null;
    }
    throw new IllegalStateException();
  }

  @Override
  @Nullable
  public ClassId getComponentType() {
    return componentType;
  }

  @Override
  public String getSimpleName() {
    switch (classType) {
    case CLASS:
    case INTERFACE:
    case PRIMITIVE:
    case VOID:
      return className;
    case ARRAY:
      return arraySimpleName;
    }
    throw new IllegalStateException();
  }

  @Override
  public String getCompleteName() {
    switch (classType) {
    case CLASS:
    case INTERFACE:
      if (packageName != null) {
        return packageName + "." + className;
      }
      return className;
    case PRIMITIVE:
    case VOID:
      return className;
    case ARRAY:
      return arrayCompleteName;
    }
    throw new IllegalStateException();
  }

  @Override
  @Nullable
  public String getCanonicalSignature() {
    return signature;
  }

  @Override
  public String getCanonicalName() {
    return canonicalName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((access == null) ? 0 : access.hashCode());
    result = prime * result
        + ((canonicalName == null) ? 0 : canonicalName.hashCode());
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    result = prime * result + ((classType == null) ? 0 : classType.hashCode());
    result = prime * result
        + ((componentType == null) ? 0 : componentType.hashCode());
    result = prime * result + id.hashCode();
    result = prime * result
        + ((interfaces == null) ? 0 : interfaces.hashCode());
    result = prime * result + ((metaId == null) ? 0 : metaId.hashCode());
    result = prime * result
        + ((packageName == null) ? 0 : packageName.hashCode());
    result = prime * result + ((signature == null) ? 0 : signature.hashCode());
    result = prime * result
        + ((superClass == null) ? 0 : superClass.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ClassImpl other = (ClassImpl) obj;
    if (access == null) {
      if (other.access != null) {
        return false;
      }
    } else if (!access.equals(other.access)) {
      return false;
    }
    if (canonicalName == null) {
      if (other.canonicalName != null) {
        return false;
      }
    } else if (!canonicalName.equals(other.canonicalName)) {
      return false;
    }
    if (className == null) {
      if (other.className != null) {
        return false;
      }
    } else if (!className.equals(other.className)) {
      return false;
    }
    if (classType != other.classType) {
      return false;
    }
    if (componentType == null) {
      if (other.componentType != null) {
        return false;
      }
    } else if (!componentType.equals(other.componentType)) {
      return false;
    }
    if (!id.equals(other.id)) {
      return false;
    }
    if (interfaces == null) {
      if (other.interfaces != null) {
        return false;
      }
    } else if (!interfaces.equals(other.interfaces)) {
      return false;
    }
    if (metaId == null) {
      if (other.metaId != null) {
        return false;
      }
    } else if (!metaId.equals(other.metaId)) {
      return false;
    }
    if (packageName == null) {
      if (other.packageName != null) {
        return false;
      }
    } else if (!packageName.equals(other.packageName)) {
      return false;
    }
    if (signature == null) {
      if (other.signature != null) {
        return false;
      }
    } else if (!signature.equals(other.signature)) {
      return false;
    }
    if (superClass == null) {
      if (other.superClass != null) {
        return false;
      }
    } else if (!superClass.equals(other.superClass)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClassImpl [id=" + id + ", metaId=" + metaId + ", access=" + access
        + ", classType=" + classType + ", className=" + className
        + ", packageName=" + packageName + ", superClass=" + superClass
        + ", interfaces=" + interfaces + ", componentType=" + componentType
        + ", signature=" + signature + ", canonicalName=" + canonicalName + "]";
  }

}
