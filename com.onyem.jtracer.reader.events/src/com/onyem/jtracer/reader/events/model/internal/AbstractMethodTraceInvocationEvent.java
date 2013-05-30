package com.onyem.jtracer.reader.events.model.internal;

import java.util.Collections;
import java.util.List;

import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodTraceInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public abstract class AbstractMethodTraceInvocationEvent extends
    AbstractInvocationEvent implements IMethodTraceInvocationEvent {

  protected final List<IMethod> methodTrace;

  AbstractMethodTraceInvocationEvent(long id, long filePosition,
      IInvocationThread thread, InvocationEventType eventType,
      List<IMethod> methodTrace) {
    super(id, filePosition, thread, eventType);
    this.methodTrace = Collections.unmodifiableList(methodTrace);
  }

  public List<IMethod> getMethodTrace() {
    return methodTrace;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result
        + ((methodTrace == null) ? 0 : methodTrace.hashCode());
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
    AbstractMethodTraceInvocationEvent other = (AbstractMethodTraceInvocationEvent) obj;
    if (methodTrace == null) {
      if (other.methodTrace != null) {
        return false;
      }
    } else if (!methodTrace.equals(other.methodTrace)) {
      return false;
    }
    return true;
  }

}
