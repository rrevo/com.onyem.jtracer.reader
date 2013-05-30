package com.onyem.jtracer.reader.events.internal.converter;

import java.util.List;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;

public interface IEventConverter {

  boolean loadMoreEvents();

  int getFetchCount();

  void convertEvents(List<IInvocationEvent> events, boolean complete);

  List<IInvocationEvent> getEvents();

}
