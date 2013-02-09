package com.onyem.jtracer.reader.events.model;

public interface IInvocationEvent {

  long getId();

  long getFilePosition();

  IInvocationThread getThread();

  InvocationEventType getType();

}
