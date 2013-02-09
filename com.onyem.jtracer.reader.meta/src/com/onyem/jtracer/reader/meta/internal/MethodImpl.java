package com.onyem.jtracer.reader.meta.internal;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.IMethod;

@Immutable
public class MethodImpl implements IMethod {

  public static IMethod createMethod(long id, Long metaId, Integer access,
      String name, IClass clazz, List<IClass> parameters, IClass returnType,
      List<IClass> exceptions, String description, String signature) {
    return new MethodImpl(id, metaId, access, name, clazz, parameters,
        returnType, exceptions, description, signature);
  }

  private final long id;
  private final Long metaId;
  private final Integer access;
  private final String name;
  private final IClass clazz;
  private final List<IClass> parameters;
  private final IClass returnType;
  private final List<IClass> exceptions;
  private final String description;
  private final String signature;

  private MethodImpl(Long id, Long metaId, Integer access, String name,
      IClass clazz, List<IClass> parameters, IClass returnType,
      List<IClass> exceptions, String description, String signature) {
    this.id = id;
    this.metaId = metaId;
    this.access = access;
    this.name = name;

    this.clazz = clazz;
    this.parameters = Collections.unmodifiableList(parameters);
    this.returnType = returnType;
    this.exceptions = Collections.unmodifiableList(exceptions);

    this.description = description;
    this.signature = signature;
  }

  @Override
  public long getId() {
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
  public String getName() {
    return name;
  }

  @Override
  public IClass getIClass() {
    return clazz;
  }

  @Override
  public List<IClass> getParameters() {
    return parameters;
  }

  @Override
  public IClass getReturn() {
    return returnType;
  }

  @Override
  @Nullable
  public List<IClass> getExceptions() {
    return exceptions;
  }

  @Override
  public String getCanonicalDescription() {
    return description;
  }

  @Override
  public String getCanonicalSignature() {
    return signature;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((access == null) ? 0 : access.hashCode());
    result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
    result = prime * result
        + ((description == null) ? 0 : description.hashCode());
    result = prime * result
        + ((exceptions == null) ? 0 : exceptions.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((metaId == null) ? 0 : metaId.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result
        + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result
        + ((returnType == null) ? 0 : returnType.hashCode());
    result = prime * result + ((signature == null) ? 0 : signature.hashCode());
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
    MethodImpl other = (MethodImpl) obj;
    if (access == null) {
      if (other.access != null) {
        return false;
      }
    } else if (!access.equals(other.access)) {
      return false;
    }
    if (clazz == null) {
      if (other.clazz != null) {
        return false;
      }
    } else if (!clazz.equals(other.clazz)) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (exceptions == null) {
      if (other.exceptions != null) {
        return false;
      }
    } else if (!exceptions.equals(other.exceptions)) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (metaId == null) {
      if (other.metaId != null) {
        return false;
      }
    } else if (!metaId.equals(other.metaId)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (parameters == null) {
      if (other.parameters != null) {
        return false;
      }
    } else if (!parameters.equals(other.parameters)) {
      return false;
    }
    if (returnType == null) {
      if (other.returnType != null) {
        return false;
      }
    } else if (!returnType.equals(other.returnType)) {
      return false;
    }
    if (signature == null) {
      if (other.signature != null) {
        return false;
      }
    } else if (!signature.equals(other.signature)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "MethodDbImpl [id=" + id + ", metaId=" + metaId + ", access="
        + access + ", name=" + name + ", clazz=" + clazz + ", parameters="
        + parameters + ", returnType=" + returnType + ", exceptions="
        + exceptions + ", description=" + description + ", signature="
        + signature + "]";
  }

}
