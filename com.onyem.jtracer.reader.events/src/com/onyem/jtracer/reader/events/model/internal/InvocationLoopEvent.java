package com.onyem.jtracer.reader.events.model.internal;

import java.util.Collections;
import java.util.List;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationLoopEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public class InvocationLoopEvent extends AbstractInvocationEvent implements
    IInvocationLoopEvent {

  private final int count;
  private final List<IInvocationEvent> events;

  public InvocationLoopEvent(long id, long filePointer,
      IInvocationThread thread, int count, List<IInvocationEvent> events) {
    super(id, filePointer, thread, InvocationEventType.Loop);
    this.count = count;
    assert count > 1;

    this.events = Collections.unmodifiableList(events);
    for (IInvocationEvent invocationEvent : events) {
      assert invocationEvent.getThread() == thread;
    }
  }

  @Override
  public int getLoopCount() {
    return count;
  }

  @Override
  public List<IInvocationEvent> getEvents() {
    return events;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + count;
    result = prime * result + ((events == null) ? 0 : events.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InvocationLoopEvent other = (InvocationLoopEvent) obj;
    if (count != other.count) {
      return false;
    }
    if (events == null) {
      if (other.events != null) {
        return false;
      }
    } else if (!events.equals(other.events)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "InvocationLoopEvent [count=" + count + ", events=" + events + "]";
  }

}