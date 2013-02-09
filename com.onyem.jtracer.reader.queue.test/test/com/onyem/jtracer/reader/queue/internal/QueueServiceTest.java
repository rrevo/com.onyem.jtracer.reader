package com.onyem.jtracer.reader.queue.internal;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import com.onyem.jtracer.reader.queue.IQueueService;

public class QueueServiceTest {

  private BlockingQueue<Integer> results = new LinkedBlockingQueue<Integer>();
  private IQueueService queueService = new QueueService();

  private SleepingRunnable run_1 = new SleepingRunnable(results, 1);
  private SleepingRunnable run_2 = new SleepingRunnable(results, 2);
  private SleepingRunnable run_4 = new SleepingRunnable(results, 4);
  private SleepingRunnable run_100 = new SleepingRunnable(results, 100);

  // Tasks added in foreground should run in order
  @Test
  public void testForeground() throws Exception {
    queueService.queueNow(run_1);
    queueService.queueNow(run_2);
    queueService.queueNow(run_4);

    Assert.assertEquals(run_1.getTime(), results.take());
    Assert.assertEquals(run_2.getTime(), results.take());
    Assert.assertEquals(run_4.getTime(), results.take());
  }

  // Tasks added in background should run in order
  @Test
  public void testBackground() throws Exception {
    queueService.queueLater(run_1);
    queueService.queueLater(run_2);
    queueService.queueLater(run_4);

    Assert.assertEquals(run_1.getTime(), results.take());
    Assert.assertEquals(run_2.getTime(), results.take());
    Assert.assertEquals(run_4.getTime(), results.take());
  }

  // This test is timing dependent
  @Test
  public void testInterleaved() throws Exception {
    // First fg add a task that takes time
    queueService.queueNow(run_100);
    // Then bg add 3 tasks
    queueService.queueLater(run_1);
    queueService.queueLater(run_2);
    // Add another fg task and it should be scheduled before the 
    // bg tasks provided that the 1st fg task does not complete
    queueService.queueNow(run_4);

    Assert.assertEquals(run_100.getTime(), results.take());
    Assert.assertEquals(run_4.getTime(), results.take());
    Assert.assertEquals(run_1.getTime(), results.take());
    Assert.assertEquals(run_2.getTime(), results.take());
  }

  @After
  public void teardown() throws IOException {
    queueService.close();
  }

  private static class SleepingRunnable implements Runnable {

    private BlockingQueue<Integer> results;
    private final int time;

    SleepingRunnable(BlockingQueue<Integer> results, int time) {
      if (time <= 0) {
        throw new IllegalArgumentException();
      }
      this.results = results;
      this.time = time;
    }

    public Integer getTime() {
      return time;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(time * 100);
      } catch (InterruptedException e) {
      }
      results.add(time);
    }

    @Override
    public String toString() {
      return "SleepingRunnable[" + time + "]";
    }
  }
}