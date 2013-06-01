package com.onyem.jtracer.reader.events.internal;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.meta.IMethod;

public class LoopEventTest extends AbstractEventTest {

  private final Factory f = new Factory();
  private final IInvocationThread t1 = f.thread(1);
  private final IInvocationThread t10 = f.thread(10);

  @Test
  public void test() {

    List<IInvocationEvent> es = eventService.getNextEvent(null);
    Assert.assertEquals(getEventCount(), es.size());
    final IMethod mAllo = metaService.getMethodByMetaId(4);
    final IMethod mSayonara = metaService.getMethodByMetaId(5);

    int index = 0;
    assertEvent(InvocationEventType.MethodEntry, 1, 2, es.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 3, es.get(index++));
    assertLoopEvent(1, es.get(index++), 10, f.en(t1, mAllo),
        f.en(t1, mSayonara), f.ex(t1, mSayonara), f.ex(t1, mAllo));
    assertEvent(InvocationEventType.MethodExit, 1, 3, es.get(index++));
    assertEvent(InvocationEventType.MethodEntry, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 1, es.get(index++));

    for (int i = 0; i < 11; i++) {
      assertEvent(InvocationEventType.MethodEntry, 1, 6, es.get(index++));
    }
    assertEvent(InvocationEventType.MethodExit, 1, 6, es.get(index++));

    es = eventService.getNextEvent(es.get(es.size() - 1));
    index = 0;
    for (int i = 0; i < 10; i++) {
      assertEvent(InvocationEventType.MethodExit, 1, 6, es.get(index++));
    }
    assertEvent(InvocationEventType.MethodEntry, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 1, es.get(index++));
    assertEvent(InvocationEventType.MethodExit, 1, 2, es.get(index++));

    assertEvent(InvocationEventType.MethodEntry, 10, 3, es.get(index++));
    assertLoopEvent(1, es.get(index++), 10, f.en(t10, mAllo),
        f.en(t10, mSayonara), f.ex(t10, mSayonara), f.ex(t10, mAllo));
    assertEvent(InvocationEventType.MethodExit, 10, 3, es.get(index++));
    Assert.assertEquals(index, es.size());
  }

  @Override
  String getEventPath() {
    return "test-data/out_runLoop/event.out";
  }

  @Override
  String getMetaPath() {
    return "test-data/out_runLoop/meta.out";
  }

  @Override
  protected boolean isLoopsEnabled() {
    return true;
  }
}
