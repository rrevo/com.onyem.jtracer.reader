package com.onyem.jtracer.reader.events.internal;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;

public class EventFile {

  private final long id;
  private final String name;
  private final IInvocationEvent firstEvent;
  private final IInvocationEvent lastEvent;

  public EventFile(long id, String name, IInvocationEvent firstEvent,
      IInvocationEvent lastEvent) {
    this.id = id;
    this.name = name;
    this.firstEvent = firstEvent;
    this.lastEvent = lastEvent;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public IInvocationEvent getFirstEvent() {
    return firstEvent;
  }

  public IInvocationEvent getLastEvent() {
    return lastEvent;
  }

  @Override
  public String toString() {
    return "EventFile [id=" + id + ", name=" + name + ", firstEvent="
        + firstEvent + ", lastEvent=" + lastEvent + "]";
  }

}
