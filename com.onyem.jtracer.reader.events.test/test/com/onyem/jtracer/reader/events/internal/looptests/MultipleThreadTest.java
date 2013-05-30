package com.onyem.jtracer.reader.events.internal.looptests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.onyem.jtracer.reader.events.internal.AbstractEventTest;
import com.onyem.jtracer.reader.events.internal.converter.LoopEventLoader;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class MultipleThreadTest extends
    TestHelper {

  ArrayList<IInvocationEvent> events = f.events(
      // @formatter:off
      f.en(t1, m1),
      f.en(t1, m2),
      f.ex(t1, m2),
      f.en(t1, m2),
      f.ex(t1, m2),
      f.en(t1, m2),
      f.ex(t1, m2),
      f.ex(t1, m1),
      f.en(t2, m1),
      f.en(t2, m2),
      f.ex(t2, m2),
      f.en(t2, m2),
      f.ex(t2, m2),
      f.ex(t2, m1)
      // @formatter:on
      );

  /*
   * Test a simple loop without special cases
   */
  @Test
  public void testSimpleLoop() {
    LoopEventLoader eventLoader = new LoopEventLoader(8);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(6, convertedEvents.size());

    assertCommonEvents(convertedEvents);
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t2.getId(),
        m1.getMetaId(), convertedEvents.get(3));
    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(4), 2,
        f.en(t2, m2), f.ex(t2, m2));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t2.getId(),
        m1.getMetaId(), convertedEvents.get(5));
  }

  private void assertCommonEvents(List<IInvocationEvent> convertedEvents) {
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m1.getMetaId(), convertedEvents.get(0));
    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(1), 3,
        f.en(t1, m2), f.ex(t1, m2));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m1.getMetaId(), convertedEvents.get(2));
  }

  /*
   * Convert only the required number of events
   */
  @Test
  public void testCount() {
    LoopEventLoader eventLoader = new LoopEventLoader(4);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(4, convertedEvents.size());

    assertCommonEvents(convertedEvents);
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t2.getId(),
        m1.getMetaId(), convertedEvents.get(3));
  }

  /*
   * Convert only the required number of events for a loop
   */
  @Test
  public void testCountLoop() {
    LoopEventLoader eventLoader = new LoopEventLoader(5);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(5, convertedEvents.size());

    assertCommonEvents(convertedEvents);
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t2.getId(),
        m1.getMetaId(), convertedEvents.get(3));
    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(4), 2,
        f.en(t2, m2), f.ex(t2, m2));
  }
}
