package com.onyem.jtracer.reader.events.model.internal;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;

@Immutable
public class InvocationEventComparator {

  /**
   * Compare two {@link IInvocationEvent}s without considering Id's and other
   * positional information
   * 
   * @param o1
   * @param o2
   * @return
   */
  public boolean compare(IInvocationEvent o1, IInvocationEvent o2) {
    if (o1.getClass() != o2.getClass()) {
      return false;
    }
    AbstractInvocationEvent ao1 = (AbstractInvocationEvent) o1;
    AbstractInvocationEvent ao2 = (AbstractInvocationEvent) o2;
    if (!ao1.getThread().equals(ao2.getThread())
        || !ao1.getType().equals(ao2.getType())) {
      return false;
    }
    if (o1 instanceof AbstractMethodInvocationEvent) {
      assert o1 instanceof MethodEntryInvocationEvent
          || o1 instanceof MethodExitInvocationEvent
          || o1 instanceof MethodThrowExitInvocationEvent;
      AbstractMethodInvocationEvent e1 = (AbstractMethodInvocationEvent) o1;
      AbstractMethodInvocationEvent e2 = (AbstractMethodInvocationEvent) o2;
      return e1.getMethod().equals(e2.getMethod());
    }
    if (o1 instanceof AbstractMethodTraceInvocationEvent) {
      assert o1 instanceof ExceptionCatchInvocationEvent
          || o1 instanceof ExceptionThrowInvocationEvent;
      AbstractMethodTraceInvocationEvent e1 = (AbstractMethodTraceInvocationEvent) o1;
      AbstractMethodTraceInvocationEvent e2 = (AbstractMethodTraceInvocationEvent) o2;
      return e1.getMethodTrace().equals(e2.getMethodTrace());
    }
    if (o1 instanceof InvocationLoopEvent) {
      InvocationLoopEvent l1 = (InvocationLoopEvent) o1;
      InvocationLoopEvent l2 = (InvocationLoopEvent) o2;
      List<IInvocationEvent> l1Events = l1.getEvents();
      List<IInvocationEvent> l2Events = l2.getEvents();
      if (l1Events.size() == l2Events.size()) {
        for (int i = 0; i < l1Events.size(); i++) {
          if (!compare(l1Events.get(i), l2Events.get(i))) {
            return false;
          }
        }
      }
      return true;
    }
    throw new IllegalArgumentException();
  }
}
