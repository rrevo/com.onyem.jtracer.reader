package com.onyem.jtracer.reader.meta.internal;

import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.IClass;

public class ClassIdImpl implements ClassId {

  private final long id;

  public ClassIdImpl(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public Class<?> getType() {
    return IClass.class;
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
    ClassIdImpl other = (ClassIdImpl) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClassIdImpl [id=" + id + "]";
  }

}
