package com.onyem.jtracer.reader.queue;

import java.io.Closeable;
import java.io.IOException;

/**
 * Runs tasks in another thread.
 * 
 * Two levels of priority are provided.
 * 
 * Runnable tasks that are submitted via queueNow will be
 * executed before tasks that are submitted via queueLater
 * 
 */
public interface IQueueService extends Closeable {

  void queueNow(Runnable task);

  void queueLater(Runnable task);

  /**
   * Discards all tasks that have not been started
   */
  @Override
  public void close() throws IOException;
}
