package com.onyem.jtracer.reader.events.internal.looptests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.onyem.jtracer.reader.events.internal.AbstractEventTest;
import com.onyem.jtracer.reader.events.internal.converter.LoopEventLoader;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationLoopEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class OtherCompleteTest extends TestHelper {

  @Test
  public void testInterleavedLoop() {
    ArrayList<IInvocationEvent> events = f.events(
        // @formatter:off
        f.en(t1, m1),
        f.en(t1, m2),
        f.ex(t1, m2),
        f.en(t1, m2),
        f.ex(t1, m2),
        f.en(t2, m1),
        f.en(t1, m2),
        f.ex(t1, m2),
        f.ex(t1, m1),
        f.en(t2, m2),
        f.ex(t2, m2),
        f.en(t2, m2),
        f.ex(t2, m2),
        f.ex(t2, m1)
        // @formatter:on
        );
    LoopEventLoader eventLoader = new LoopEventLoader(8);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();

    int index = 0;
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m1.getMetaId(), convertedEvents.get(index++));
    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(index++),
        2, f.en(t1, m2), f.ex(t1, m2));
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t2.getId(),
        m1.getMetaId(), convertedEvents.get(index++));

    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m2.getMetaId(), convertedEvents.get(index++));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m2.getMetaId(), convertedEvents.get(index++));

    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m1.getMetaId(), convertedEvents.get(index++));
    AbstractEventTest.assertLoopEvent(t1.getId(), convertedEvents.get(index++),
        2, f.en(t2, m2), f.ex(t2, m2));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t2.getId(),
        m1.getMetaId(), convertedEvents.get(index++));

    Assert.assertEquals(index, convertedEvents.size());
  }

  /*
   * Case where the same method is nested and recursed
   */
  @Test
  public void testRepeatedNestedLoop() {
    ArrayList<IInvocationEvent> events = f.events(
// @formatter:off
        f.en(t1, m1),
          f.en(t1, m2),
            f.en(t1, m1),
              f.en(t1, m2),
              f.ex(t1, m2),
            f.ex(t1, m1),
            
            f.en(t1, m1),
              f.en(t1, m2),
              f.ex(t1, m2),
            f.ex(t1, m1),
            
            f.en(t1, m1),
              f.en(t1, m2),
              f.ex(t1, m2),
            f.ex(t1, m1),
          f.ex(t1, m2),
        f.ex(t1, m1),
        
        f.en(t1, m1),
          f.en(t1, m2),
            f.en(t1, m1),
              f.en(t1, m2),
              f.ex(t1, m2),
            f.ex(t1, m1),
            
            f.en(t1, m1),
              f.en(t1, m2),
              f.ex(t1, m2),
            f.ex(t1, m1),
            
            f.en(t1, m1),
              f.en(t1, m2),
              f.ex(t1, m2),
            f.ex(t1, m1),
          f.ex(t1, m2),
        f.ex(t1, m1)
        // @formatter:on
        );
    LoopEventLoader eventLoader = new LoopEventLoader(8);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();
    Assert.assertEquals(1, convertedEvents.size());

    IInvocationLoopEvent loopEvent = (IInvocationLoopEvent) convertedEvents
        .get(0);
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
    Assert.assertEquals(3, loopEvent.getLoopCount());

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
  }

  /**
   * Simulate a method call like
   * void m0() {
   * m1();
   * m1();
   * }
   * void m1() {
   * m2();
   * m2();
   * m2();
   * }
   * void m2() {
   * m3();
   * m3();
   * }
   * void m3() {
   * }
   */
  @Test
  public void testNestedLoop() {
    ArrayList<IInvocationEvent> events = f.events(
        // @formatter:off
        f.en(t1, m0),
          f.en(t1, m1),
            f.en(t1, m2),
              f.en(t1, m3),
              f.ex(t1, m3),
              f.en(t1, m3),
              f.ex(t1, m3),
            f.ex(t1, m2),
            f.en(t1, m2),
              f.en(t1, m3),
              f.ex(t1, m3),
              f.en(t1, m3),
              f.ex(t1, m3),
            f.ex(t1, m2),
            f.en(t1, m2),
              f.en(t1, m3),
              f.ex(t1, m3),
              f.en(t1, m3),
              f.ex(t1, m3),
            f.ex(t1, m2),
          f.ex(t1, m1),
          f.en(t1, m1),
            f.en(t1, m2),
              f.en(t1, m3),
              f.ex(t1, m3),
              f.en(t1, m3),
              f.ex(t1, m3),
            f.ex(t1, m2),
            f.en(t1, m2),
              f.en(t1, m3),
              f.ex(t1, m3),
              f.en(t1, m3),
              f.ex(t1, m3),
            f.ex(t1, m2),
            f.en(t1, m2),
              f.en(t1, m3),
              f.ex(t1, m3),
              f.en(t1, m3),
              f.ex(t1, m3),
            f.ex(t1, m2),
          f.ex(t1, m1),
        f.ex(t1, m0)
        // @formatter:on
        );
    LoopEventLoader eventLoader = new LoopEventLoader(8);
    eventLoader.convertEvents(events, true);
    List<IInvocationEvent> convertedEvents = eventLoader.getEvents();

    Assert.assertEquals(3, convertedEvents.size());
    AbstractEventTest.assertEvent(InvocationEventType.MethodEntry, t1.getId(),
        m0.getMetaId(), convertedEvents.get(0));
    AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
        m0.getMetaId(), convertedEvents.get(2));

    {
      IInvocationLoopEvent l0Loop = (IInvocationLoopEvent) convertedEvents
          .get(1);
      Assert.assertEquals(2, l0Loop.getLoopCount());

      List<IInvocationEvent> l0Events = l0Loop.getEvents();
      Assert.assertEquals(3, l0Events.size());

      AbstractEventTest.assertEvent(InvocationEventType.MethodEntry,
          t1.getId(), m1.getMetaId(), l0Events.get(0));
      AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
          m1.getMetaId(), l0Events.get(2));
    }
    {
      IInvocationLoopEvent l1Loop = (IInvocationLoopEvent) ((IInvocationLoopEvent) convertedEvents
          .get(1)).getEvents().get(1);
      Assert.assertEquals(3, l1Loop.getLoopCount());

      List<IInvocationEvent> l1Events = l1Loop.getEvents();
      Assert.assertEquals(3, l1Events.size());

      AbstractEventTest.assertEvent(InvocationEventType.MethodEntry,
          t1.getId(), m2.getMetaId(), l1Events.get(0));
      AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
          m2.getMetaId(), l1Events.get(2));
    }
    {
      IInvocationLoopEvent l2Loop = (IInvocationLoopEvent) ((IInvocationLoopEvent) ((IInvocationLoopEvent) convertedEvents
          .get(1)).getEvents().get(1)).getEvents().get(1);
      Assert.assertEquals(2, l2Loop.getLoopCount());

      List<IInvocationEvent> l1Events = l2Loop.getEvents();
      Assert.assertEquals(2, l1Events.size());

      AbstractEventTest.assertEvent(InvocationEventType.MethodEntry,
          t1.getId(), m3.getMetaId(), l1Events.get(0));
      AbstractEventTest.assertEvent(InvocationEventType.MethodExit, t1.getId(),
          m3.getMetaId(), l1Events.get(1));
    }

  }

}
