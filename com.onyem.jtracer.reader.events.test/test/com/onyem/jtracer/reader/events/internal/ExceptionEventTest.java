package com.onyem.jtracer.reader.events.internal;

import java.util.List;

import org.junit.Test;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class ExceptionEventTest extends AbstractEventTest {

  @Test
  public void test() {
    IInvocationEvent startEvent = null;
    List<IInvocationEvent> events = null;
    for (int i = 0; i < 5; i++) {
      events = eventService.getNextEvent(startEvent);
      startEvent = events.get(events.size() - 1);
    }
    assertEvent(InvocationEventType.MethodThrowExit, 1, 18, events.get(8));
    assertEvent(InvocationEventType.MethodThrowExit, 10, 18, events.get(15));
  }

  @Override
  String getEventPath() {
    return "test-data/out_runException/event.out";
  }

  @Override
  String getMetaPath() {
    return "test-data/out_runException/meta.out";
  }
}
