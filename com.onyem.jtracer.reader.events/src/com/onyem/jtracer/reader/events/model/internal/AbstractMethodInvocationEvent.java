package com.onyem.jtracer.reader.events.model.internal;

import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public abstract class AbstractMethodInvocationEvent extends
    AbstractInvocationEvent implements IMethodInvocationEvent {

  protected final IMethod method;

  AbstractMethodInvocationEvent(long id, long filePosition,
      IInvocationThread thread, InvocationEventType eventType, IMethod method) {
    super(id, filePosition, thread, eventType);
    this.method = method;
  }

  @Override
  public IMethod getMethod() {
    return method;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AbstractMethodInvocationEvent other = (AbstractMethodInvocationEvent) obj;
    if (method == null) {
      if (other.method != null) {
        return false;
      }
    } else if (!method.equals(other.method)) {
      return false;
    }
    return true;
  }

}
