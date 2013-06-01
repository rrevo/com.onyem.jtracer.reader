package com.onyem.jtracer.reader.events.internal.looptests;

import java.util.concurrent.atomic.AtomicLong;

import com.onyem.jtracer.reader.events.internal.Factory;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.meta.IMethod;

public class TestHelper {

  private final AtomicLong generator = new AtomicLong();

  final Factory f = new Factory();
  final IMethod m0 = f.method(generator.getAndIncrement(),
      generator.getAndIncrement());
  final IMethod m1 = f.method(generator.getAndIncrement(),
      generator.getAndIncrement());
  final IMethod m2 = f.method(generator.getAndIncrement(),
      generator.getAndIncrement());
  final IMethod m3 = f.method(generator.getAndIncrement(),
      generator.getAndIncrement());
  final IInvocationThread t1 = f.thread(generator.getAndIncrement());
  final IInvocationThread t2 = f.thread(generator.getAndIncrement());

}
