package com.onyem.jtracer.reader.events.model.internal;

import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public class MethodExitInvocationEvent extends AbstractMethodInvocationEvent
    implements IMethodExitInvocationEvent {

  public MethodExitInvocationEvent(long id, long filePosition,
      IInvocationThread thread, IMethod method) {
    super(id, filePosition, thread, InvocationEventType.MethodExit, method);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
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
    return true;
  }

  @Override
  public String toString() {
    return "MethodExitInvocationEvent [id=" + id + ", type=" + type
        + ", filePosition=" + filePosition + ", thread=" + thread + ", method="
        + method + "]";
  }
}
