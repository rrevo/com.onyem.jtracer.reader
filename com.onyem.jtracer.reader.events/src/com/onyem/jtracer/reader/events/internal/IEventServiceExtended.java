package com.onyem.jtracer.reader.events.internal;

import com.onyem.jtracer.reader.events.IEventService;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;

public interface IEventServiceExtended extends IEventService {

  int DEFAULT_EVENTS_COUNT = 20;

  IInvocationEvent getEventById(long id);

  IInvocationThread getOrInsertThreadById(long threadId);

  EventFile getEventFileByName(String name);

}
