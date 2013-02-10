package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitInvocationEvent;
import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.IImageManager;

public class EventFigureFactory {

  private final IImageManager imageManager;

  public EventFigureFactory() {
    imageManager = Activator.getImageManager();
  }

  public InvocationEventFigure create(IInvocationEvent invocationEvent,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    switch (invocationEvent.getType()) {
    case MethodEntry:
      return new MethodEntryInvocationFigure(imageManager,
          (IMethodEntryInvocationEvent) invocationEvent, previousEventFigure,
          previousThreadFigure);

    case MethodExit:
      return new MethodExitInvocationFigure(imageManager,
          (IMethodExitInvocationEvent) invocationEvent, previousEventFigure,
          previousThreadFigure);

    default:
      throw new IllegalArgumentException();
    }
  }
}
