package com.onyem.jtracer.reader.events.internal;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class EventsWithSmallFileTest extends AbstractEventTest {

  private String getEventFileName() {
    return "event00.out";
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
    List<IInvocationEvent> events = eventService.getNextEvent(null);
    assertEvents(events);

    // EventFile state
    eventFile = eventService.getEventFileByName(getEventFileName());
    Assert.assertEquals(1, eventFile.getId());
    Assert.assertEquals(getEventFileName(), eventFile.getName());
    Assert.assertEquals(events.get(0), eventFile.getFirstEvent());
    Assert.assertEquals(events.get(9), eventFile.getLastEvent());

    // Get the events again. This time should come from the DB
    events = eventService.getNextEvent(null);
    assertEvents(events);
  }

  private void assertEvents(List<IInvocationEvent> events) {
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
    assertEvent(InvocationEventType.MethodExit, 1, 3, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 2, events.get(index++));
  }

  private void assertEvent(InvocationEventType type, long threadId,
      long methodId, IInvocationEvent event) {
    IMethodInvocationEvent methodEvent = (IMethodInvocationEvent) event;
    Assert.assertEquals(type, methodEvent.getType());
    Assert.assertEquals(threadId, methodEvent.getThread().getId());
    Assert.assertEquals(methodId, methodEvent.getMethod().getMetaId()
        .longValue());
  }
}
