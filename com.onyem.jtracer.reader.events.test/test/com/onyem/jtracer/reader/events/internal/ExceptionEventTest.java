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
    int index = 0;
    assertEvent(InvocationEventType.MethodEntry, 1, 17, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 22, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 7, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 5, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 5, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 7, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 22, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 18, events.get(index++));
    assertEvent(InvocationEventType.MethodThrowExit, 1, 18, events.get(index++));

    assertTraceEvent(events.get(index++), InvocationEventType.ExceptionThrow,
        1, new String[] { "org/world/Util", "level5",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level4",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level3",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level2",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level1",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level0",
            "(ILjava/lang/Runnable;)V", "org/world/HelloWorldException",
            "sayIterHello", "(Ljava/lang/String;I)V",
            "org/world/HelloWorldException", "run", "()V",
            "org/world/HelloWorldException", "main", "([Ljava/lang/String;)V" });

    assertTraceEvent(events.get(index++), InvocationEventType.ExceptionCatch,
        1, new String[] { "org/world/HelloWorldException", "run", "()V",
            "org/world/HelloWorldException", "main", "([Ljava/lang/String;)V" });

    assertEvent(InvocationEventType.MethodEntry, 1, 5, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 5, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 3, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 2, events.get(index++));
    assertEvent(InvocationEventType.MethodExit, 10, 22, events.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 10, 18, events.get(index++));
    assertEvent(InvocationEventType.MethodThrowExit, 10, 18,
        events.get(index++));

    assertTraceEvent(events.get(index++), InvocationEventType.ExceptionThrow,
        10, new String[] { "org/world/Util", "level5",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level4",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level3",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level2",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level1",
            "(ILjava/lang/Runnable;)V", "org/world/Util", "level0",
            "(ILjava/lang/Runnable;)V", "org/world/HelloWorldException",
            "sayIterHello", "(Ljava/lang/String;I)V",
            "org/world/HelloWorldException", "access$000",
            "(Lorg/world/HelloWorldException;Ljava/lang/String;I)V",
            "org/world/HelloWorldException$1", "run", "()V" });
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
