package com.onyem.jtracer.reader.events.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.InvocationThread;
import com.onyem.jtracer.reader.events.model.internal.MethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodExitInvocationEvent;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.MethodId;

public class Factory {

  private static final int NULL_POSITION = -1;
  private final AtomicLong generator;

  public Factory() {
    this(0);
  }

  public Factory(long initialValue) {
    generator = new AtomicLong(initialValue);
  }

  public IMethodEntryInvocationEvent en(IInvocationThread thread, IMethod method) {
    return new MethodEntryInvocationEvent(generator.getAndIncrement(),
        NULL_POSITION, thread, method);
  }

  public IMethodExitInvocationEvent ex(IInvocationThread thread, IMethod method) {
    return new MethodExitInvocationEvent(generator.getAndIncrement(),
        NULL_POSITION, thread, method);
  }

  public ArrayList<IInvocationEvent> events(IInvocationEvent... event) {
    return new ArrayList<IInvocationEvent>(Arrays.asList(event));
  }

  public IInvocationThread thread(long threadId) {
    return new InvocationThread(threadId);
  }

  public IMethod method(long methodId, long metaId) {
    MethodId mId = mock(MethodId.class);
    when(mId.getId()).thenReturn(methodId);

    IMethod method = mock(IMethod.class);
    when(method.getId()).thenReturn(mId);
    when(method.getMetaId()).thenReturn(Long.valueOf(metaId));
    return method;
  }
}
