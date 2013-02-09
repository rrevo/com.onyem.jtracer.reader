package com.onyem.jtracer.reader.ui.editors.trace.model.figure;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;

public class EventTraceFigureModel extends Observable {

  private List<IInvocationEvent> events = new ArrayList<IInvocationEvent>();

  public synchronized void addEvents(List<IInvocationEvent> newEvents) {
    events.addAll(newEvents);
    setChanged();
    notifyObservers(newEvents);
  }

  public synchronized List<IInvocationEvent> getEvents() {
    return events;
  }
}
