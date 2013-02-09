package com.onyem.jtracer.reader.events.internal;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class EventsWithMediumFileTest extends AbstractEventTest {

  private String getEventFileName() {
    return "event01.out";
  }

  @Override
  protected int getEventCount() {
    return 10;
  }

  @Override
  String getEventPath() {
    return "test-data/out_runMultithreaded/" + getEventFileName();
  }

  @Override
  String getMetaPath() {
    return "test-data/out_runMultithreaded/meta.out";
  }

  @Test
  public void test() {
    // In a fresh DB nothing is loaded
    EventFile eventFile = eventService.getEventFileByName(getEventFileName());
    Assert.assertNull(eventFile);
    IInvocationEvent firstEvent = null;
    IInvocationEvent prevLastEvent = null;
    {
      List<IInvocationEvent> events = eventService.getNextEvent(null);
      assertEvents0(events);

      firstEvent = events.get(0);
      // EventFile state
      eventFile = eventService.getEventFileByName(getEventFileName());
      assertEventsFile(1, firstEvent, null, eventFile);

      // Get the events again. This time should come from the DB
      events = eventService.getNextEvent(null);
      assertEvents0(events);

      prevLastEvent = events.get(9);
    }

    // Second batch of events
    {
      List<IInvocationEvent> events = eventService.getNextEvent(prevLastEvent);
      assertEvents1(events);

      // EventFile state
      eventFile = eventService.getEventFileByName(getEventFileName());
      assertEventsFile(1, firstEvent, null, eventFile);

      events = eventService.getNextEvent(prevLastEvent);
      assertEvents1(events);

      prevLastEvent = events.get(9);
    }

    // Third batch of events
    {
      List<IInvocationEvent> events = eventService.getNextEvent(prevLastEvent);
      assertEvents2(events);

      // EventFile state
      eventFile = eventService.getEventFileByName(getEventFileName());
      assertEventsFile(1, firstEvent, null, eventFile);

      events = eventService.getNextEvent(prevLastEvent);
      assertEvents2(events);

      prevLastEvent = events.get(9);
    }

    // Fourth batch of events
    {
      List<IInvocationEvent> events = eventService.getNextEvent(prevLastEvent);
      assertEvents3(events);

      // EventFile state
      eventFile = eventService.getEventFileByName(getEventFileName());
      assertEventsFile(1, firstEvent, events.get(3), eventFile);

      events = eventService.getNextEvent(prevLastEvent);
      assertEvents3(events);
    }

  }

  private void assertEventsFile(long id, IInvocationEvent firstEvent,
      IInvocationEvent lastEvent, EventFile eventFile) {
    Assert.assertEquals(id, eventFile.getId());
    Assert.assertEquals(getEventFileName(), eventFile.getName());
    Assert.assertEquals(firstEvent, eventFile.getFirstEvent());
    Assert.assertEquals(lastEvent, eventFile.getLastEvent());
  }

  //  <+|1|2>
  //  <+|1|1>
  //  <-|1|1>
  //  <+|1|3>
  //  <+|1|9>
  //  <-|1|9>
  //  <+|1|9>
  //  <-|1|9>
  //  <+|10|10>
  //  <+|10|4>
  private void assertEvents0(List<IInvocationEvent> events) {
    Assert.assertEquals(10, events.size());
    int index = 0;
    assertEvent(InvocationEventType.MethodEntry, 1, 2, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 1, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 1, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 3, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 9, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 9, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 9, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 9, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 10, 10, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 10, 4, events.get(index++));
  }

  //  <+|11|10>
  //  <+|11|4>
  //  <-|1|3>
  //  <-|1|2>
  //  <-|11|4>
  //  <-|10|4>
  //  <+|10|4>
  //  <-|10|4>
  //  <+|11|4>
  //  <-|11|4>
  private void assertEvents1(List<IInvocationEvent> events) {
    Assert.assertEquals(10, events.size());
    int index = 0;
    assertEvent(InvocationEventType.MethodEntry, 11, 10, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 3, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 2, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 11, 4, events.get(index++));
  }

  //  <+|10|4>
  //  <-|10|4>
  //  <+|11|4>
  //  <-|11|4>
  //  <+|10|4>
  //  <-|10|4>
  //  <+|10|4>
  //  <-|10|4>
  //  <+|11|4>
  //  <-|11|4>
  private void assertEvents2(List<IInvocationEvent> events) {
    Assert.assertEquals(10, events.size());
    int index = 0;
    assertEvent(InvocationEventType.MethodEntry, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 10, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 11, 4, events.get(index++));
  }

  //  <-|10|10>
  //  <+|11|4>
  //  <-|11|4>
  //  <-|11|10>
  private void assertEvents3(List<IInvocationEvent> events) {
    Assert.assertEquals(4, events.size());
    int index = 0;
    assertEvent(InvocationEventType.MethodExit, 10, 10, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 11, 4, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 11, 10, events.get(index++));
  }
}
