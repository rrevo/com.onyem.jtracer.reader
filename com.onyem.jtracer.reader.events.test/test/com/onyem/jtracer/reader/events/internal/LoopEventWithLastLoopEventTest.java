package com.onyem.jtracer.reader.events.internal;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationLoopEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public class LoopEventWithLastLoopEventTest extends AbstractEventTest {

  private final Factory f = new Factory();
  private final IInvocationThread t1 = f.thread(1);

  @Test
  public void test() {

    List<IInvocationEvent> es = eventService.getNextEvent(null);
    Assert.assertEquals(getEventCount(), es.size());
    final IMethod m1 = metaService.getMethodByMetaId(4);
    final IMethod m2 = metaService.getMethodByMetaId(5);

    int index = 0;
    assertEvent(InvocationEventType.MethodEntry, 1, 2, es.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 3, es.get(index++));

    IInvocationLoopEvent loopEvent = (IInvocationLoopEvent) es.get(index++);
    Assert.assertEquals(2, loopEvent.getLoopCount());

    List<IInvocationEvent> loopEvents = loopEvent.getEvents();
    Assert.assertEquals(5, loopEvents.size());

    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m1.getMetaId(), loopEvents.get(0));
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m2.getMetaId(), loopEvents.get(1));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m2.getMetaId(), loopEvents.get(3));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m1.getMetaId(), loopEvents.get(4));

    loopEvent = (IInvocationLoopEvent) loopEvents.get(2);
    Assert.assertEquals(2, loopEvent.getLoopCount());

    loopEvents = loopEvent.getEvents();
    Assert.assertEquals(4, loopEvents.size());

    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m1.getMetaId(), loopEvents.get(0));
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m2.getMetaId(), loopEvents.get(1));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m2.getMetaId(), loopEvents.get(2));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m1.getMetaId(), loopEvents.get(3));

    Assert.assertEquals(index, es.size());

    index = 0;
    es = eventService.getNextEvent(es.get(es.size() - 1));
    assertEvent(InvocationEventType.MethodExit, 1, 3, es.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 2, es.get(index++));
    Assert.assertEquals(index, es.size());
  }

  @Override
  String getEventPath() {
    return "test-data/out_runLoop/event2.out";
  }

  @Override
  String getMetaPath() {
    return "test-data/out_runLoop/meta.out";
  }

  @Override
  protected boolean isLoopsEnabled() {
    return true;
  }

  @Override
  protected int getEventCount() {
    return 5;
  }

}
