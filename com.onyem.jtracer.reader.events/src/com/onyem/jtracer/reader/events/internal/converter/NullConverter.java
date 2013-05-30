package com.onyem.jtracer.reader.events.internal.converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;

/*
 * The events are aggregated as is in this converter without any transformations.
 */
@NotThreadSafe
public class NullConverter implements IEventConverter {

  private final int count;
  private final List<IInvocationEvent> events;

  public NullConverter(int count) {
    this.count = count;
    events = new ArrayList<IInvocationEvent>();
  }

  @Override
  public int getFetchCount() {
    int fetchCount = count - events.size();
    return (fetchCount < 0) ? 0 : fetchCount;
  }

  @Override
  public boolean loadMoreEvents() {
    return events.size() < count;
  }

  @Override
  public void convertEvents(List<IInvocationEvent> events, boolean complete) {
    this.events.addAll(events);
  }

  @Override
  public List<IInvocationEvent> getEvents() {
    return events;
  }

}
