package com.onyem.jtracer.reader.events;

import java.io.Closeable;
import java.util.List;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;

public interface IEventService extends Closeable {

  /*
   * Returns List of IInvocationEvents after the startEvent
   * To get the first events, startEvent == null
   * For the last event in a event file, an empty List is returned
   */
  public List<IInvocationEvent> getNextEvent(IInvocationEvent startEvent);

}
