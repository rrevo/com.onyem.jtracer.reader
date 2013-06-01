package com.onyem.jtracer.reader.events;

public class EventLoadOptions {

  public final static int DEFAULT_EVENTS_COUNT = 20;

  private boolean enableLoopEvents = true;
  private int eventsLoadCount = DEFAULT_EVENTS_COUNT;

  public boolean isEnableLoopEvents() {
    return enableLoopEvents;
  }

  public void setEnableLoopEvents(boolean enableLoopEvents) {
    this.enableLoopEvents = enableLoopEvents;
  }

  public int getEventsLoadCount() {
    return eventsLoadCount;
  }

  public void setEventsLoadCount(int eventsLoadCount) {
    this.eventsLoadCount = eventsLoadCount;
  }

}
