package com.onyem.jtracer.reader.events.internal.looptests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.onyem.jtracer.reader.events.internal.AbstractEventTest;
import com.onyem.jtracer.reader.events.internal.converter.LoopEventLoader;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class PartialSingleThreadTest extends TestHelper {

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
   * Test a simple loop with partial loop
   */
  @Test
  public void testSimpleLoop() {
    LoopEventLoader eventLoader = new LoopEventLoader(4);
    eventLoader.convertEvents(events.subList(0, 4), false);
    Assert.assertTrue(eventLoader.getEvents().isEmpty());

    eventLoader.convertEvents(events.subList(4, events.size()), true);

    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m1.getMetaId(), convertedEvents.get(0));
    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(1), 3,
        f.en(t1, m2), f.ex(t1, m2));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m1.getMetaId(), convertedEvents.get(2));
  }

}
