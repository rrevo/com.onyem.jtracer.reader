package com.onyem.jtracer.reader.meta.internal;

import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.MethodId;

public class MethodIdImpl implements MethodId {

  private final long id;

  public MethodIdImpl(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public Class<?> getType() {
    return IMethod.class;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
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
    MethodIdImpl other = (MethodIdImpl) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "MethodIdImpl [id=" + id + "]";
  }

}
