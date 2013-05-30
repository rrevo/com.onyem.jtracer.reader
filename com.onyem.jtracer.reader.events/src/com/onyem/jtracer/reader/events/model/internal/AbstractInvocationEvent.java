package com.onyem.jtracer.reader.events.model.internal;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;

public abstract class AbstractInvocationEvent implements IInvocationEvent {

  protected final long id;
  protected final long filePosition;
  protected final IInvocationThread thread;
  protected final InvocationEventType type;

  AbstractInvocationEvent(long id, long filePosition, IInvocationThread thread,
      InvocationEventType eventType) {
    this.id = id;
    this.filePosition = filePosition;
    this.thread = thread;
    this.type = eventType;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public long getFilePosition() {
    return filePosition;
  }

  @Override
  public IInvocationThread getThread() {
    return thread;
  }

  @Override
  public InvocationEventType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (filePosition ^ (filePosition >>> 32));
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((thread == null) ? 0 : thread.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AbstractInvocationEvent other = (AbstractInvocationEvent) obj;
    if (filePosition != other.filePosition) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (thread == null) {
      if (other.thread != null) {
        return false;
      }
    } else if (!thread.equals(other.thread)) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

}
