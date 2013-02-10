package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.draw2d.Figure;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.ui.IImageManager;

public abstract class InvocationEventFigure extends Figure {

  protected final IImageManager imageManager;

  protected final IInvocationThread thread;
  protected final IInvocationEvent event;

  protected InvocationEventFigure previousEventFigure;
  protected InvocationEventFigure nextEventFigure;

  protected InvocationEventFigure previousThreadFigure;
  protected InvocationEventFigure nextThreadFigure;

  InvocationEventFigure(IImageManager imageManager, IInvocationEvent event,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    this.imageManager = imageManager;
    this.event = event;
    this.thread = event.getThread();

    this.previousEventFigure = previousEventFigure;
    this.previousThreadFigure = previousThreadFigure;
  }

  public IInvocationThread getThread() {
    return thread;
  }

  public IInvocationEvent getInvocationEvent() {
    return event;
  }

  public InvocationEventFigure getPreviousStreamFigure() {
    return previousEventFigure;
  }

  void setNextStreamFigure(InvocationEventFigure nextEventFigure) {
    //    this.nextEventFigure = nextEventFigure;
    throw new UnsupportedOperationException();
  }

  public InvocationEventFigure getNextEventFigure() {
    //    return nextEventFigure;
    throw new UnsupportedOperationException();
  }

  public InvocationEventFigure getPreviousThreadFigure() {
    return previousThreadFigure;
  }

  void setNextThreadFigure(InvocationEventFigure nextThreadFigure) {
    //    this.nextThreadFigure = nextThreadFigure;
    throw new UnsupportedOperationException();
  }

  public InvocationEventFigure getNextThreadFigure() {
    //    return nextThreadFigure;
    throw new UnsupportedOperationException();
  }

  public abstract int getPreIndent();

  public abstract int getPostIndent();

}
