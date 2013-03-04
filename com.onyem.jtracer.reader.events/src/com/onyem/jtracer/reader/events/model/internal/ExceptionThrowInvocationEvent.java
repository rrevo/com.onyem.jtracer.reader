package com.onyem.jtracer.reader.events.model.internal;

import java.util.List;

import com.onyem.jtracer.reader.events.model.IExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public class ExceptionThrowInvocationEvent extends
    AbstractMethodTraceInvocationEvent implements
    IExceptionThrowInvocationEvent {

  public ExceptionThrowInvocationEvent(long id, long filePosition,
      IInvocationThread thread, List<IMethod> methodTrace) {
    super(id, filePosition, thread, InvocationEventType.ExceptionThrow,
        methodTrace);
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
    return "ExceptionThrowInvocationEvent [methodTrace=" + methodTrace
        + ", id=" + id + ", filePosition=" + filePosition + ", thread="
        + thread + ", type=" + type + "]";
  }

}
