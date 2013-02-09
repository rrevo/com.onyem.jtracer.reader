package com.onyem.jtracer.reader.queue.internal;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import com.google.inject.Inject;
import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.queue.IQueueService;

@Service
@ThreadSafe
public class QueueService implements IQueueService {

  private final Object lock = new Object();
  private int count = 0;
  private final Queue<Runnable> backgroundQueue;
  private final ExecutorService executorService;

  @Inject
  QueueService() {
    backgroundQueue = new LinkedBlockingQueue<Runnable>();
    executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>()) {

      protected void afterExecute(Runnable r, Throwable t) {

        super.afterExecute(r, t);
        synchronized (lock) {
          count = count - 1;
          checkBackgroundQueue();
        }
      }
    };
  }

  @Override
  public void close() throws IOException {
    synchronized (lock) {
      backgroundQueue.clear();
      executorService.shutdownNow();
    }
  }

  @Override
  public void queueNow(Runnable task) {
    synchronized (lock) {
      count = count + 1;
      executorService.submit(task);
    }
  }

  @Override
  public void queueLater(Runnable task) {
    synchronized (lock) {
      backgroundQueue.add(task);
      checkBackgroundQueue();
    }
  }

  private void checkBackgroundQueue() {
    synchronized (lock) {
      if (count == 0 && !backgroundQueue.isEmpty()) {
        count = count + 1;
        executorService.submit(backgroundQueue.poll());
      }
    }
  }
}
