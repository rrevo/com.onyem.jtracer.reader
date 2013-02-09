package com.onyem.jtracer.reader.queue.factory;

import com.google.inject.AbstractModule;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.queue.internal.QueueService;

public class QueueModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IQueueService.class).to(QueueService.class);
  }
}
