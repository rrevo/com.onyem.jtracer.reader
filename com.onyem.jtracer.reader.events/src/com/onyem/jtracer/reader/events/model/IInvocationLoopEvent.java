package com.onyem.jtracer.reader.events.model;

import java.util.List;

public interface IInvocationLoopEvent extends IInvocationEvent {

  public abstract int getLoopCount();

  public abstract List<IInvocationEvent> getEvents();

}