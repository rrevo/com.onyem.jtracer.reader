package com.onyem.jtracer.reader.events.model.internal;

import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public class MethodEntryInvocationEvent extends AbstractMethodInvocationEvent
    implements IMethodEntryInvocationEvent {

  public MethodEntryInvocationEvent(long id, long filePosition,
      IInvocationThread thread, IMethod method) {
    super(id, filePosition, thread, InvocationEventType.MethodEntry, method);
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
    return "MethodEntryInvocationEvent [id=" + id + ", type=" + type
        + ", filePosition=" + filePosition + ", thread=" + thread + ", method="
        + method + "]";
  }

}
