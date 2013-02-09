package com.onyem.jtracer.reader.events.internal;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.queue.IQueueService;

public class LoadEventsTest extends AbstractEventTest {

  private String getEventFileName() {
    return "event01.out";
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
  public void loadTest() {
    MyQueueService queueService = new MyQueueService();

    // Start loading a file
    eventService.loadEvents(queueService);

    // Since the whole file is loaded, assert the first and last events
    EventFile eventFile = eventService.getEventFileByName(getEventFileName());
    assertEvent(InvocationEventType.MethodEntry, 1, 2,
        eventFile.getFirstEvent());
    assertEvent(InvocationEventType.MethodExit, 11, 10,
        eventFile.getLastEvent());

    Assert.assertEquals(4, queueService.runCount);
  }

  /*
   * Runs the tasks in the current thread
   */
  private static class MyQueueService implements IQueueService {

    public int runCount = 0;

    @Override
    public void queueNow(Runnable task) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void queueLater(Runnable task) {
      runCount++;
      task.run();
    }

    @Override
    public void close() throws IOException {
    }
  };

}
