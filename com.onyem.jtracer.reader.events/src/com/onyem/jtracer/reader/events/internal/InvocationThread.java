package com.onyem.jtracer.reader.events.internal;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.events.model.IInvocationThread;

@Immutable
public class InvocationThread implements IInvocationThread {

  private final long id;

  public InvocationThread(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
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
    InvocationThread other = (InvocationThread) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "InvocationThread [id=" + id + "]";
  }

}
