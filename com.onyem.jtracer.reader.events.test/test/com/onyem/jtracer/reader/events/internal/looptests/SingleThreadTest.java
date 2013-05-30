package com.onyem.jtracer.reader.events.internal.looptests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.onyem.jtracer.reader.events.internal.AbstractEventTest;
import com.onyem.jtracer.reader.events.internal.converter.LoopEventLoader;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class SingleThreadTest extends
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
      f.ex(t1, m1)
      // @formatter:on
      );

  /*
   * Test a simple loop without special cases
   */
  @Test
  public void testSimpleLoop() {
    LoopEventLoader eventLoader = new LoopEventLoader(4);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(3, convertedEvents.size());

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
    LoopEventLoader eventLoader = new LoopEventLoader(1);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(1, convertedEvents.size());

    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m1.getMetaId(), convertedEvents.get(0));
  }

  /*
   * Convert only the required number of events for a loop
   */
  @Test
  public void testCountLoop() {
    LoopEventLoader eventLoader = new LoopEventLoader(1);
    events.remove(0);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(1, convertedEvents.size());

    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(0), 3,
        f.en(t1, m2), f.ex(t1, m2));
  }
}
