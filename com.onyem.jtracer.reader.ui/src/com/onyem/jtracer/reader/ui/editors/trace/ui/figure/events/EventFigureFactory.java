package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import com.onyem.jtracer.reader.events.model.IExceptionCatchInvocationEvent;
import com.onyem.jtracer.reader.events.model.IExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitThrowInvocationEvent;
import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.IClassTraceChecker;

public class EventFigureFactory {

  private final IImageManager imageManager;
  private final IClassTraceChecker classTraceChecker;

  public EventFigureFactory(IClassTraceChecker classTraceChecker) {
    imageManager = Activator.getImageManager();
    this.classTraceChecker = classTraceChecker;
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

    case MethodThrowExit:
      return new MethodExitThrowInvocationFigure(imageManager,
          (IMethodExitThrowInvocationEvent) invocationEvent,
          previousEventFigure, previousThreadFigure);

    case ExceptionThrow:
      return new ExceptionThrowInvocationFigure(imageManager,
          (IExceptionThrowInvocationEvent) invocationEvent,
          previousEventFigure, previousThreadFigure);

    case ExceptionCatch:
      return new ExceptionCatchInvocationFigure(imageManager,
          (IExceptionCatchInvocationEvent) invocationEvent,
          previousEventFigure, previousThreadFigure, classTraceChecker);

    default:
      throw new IllegalArgumentException();
    }
  }
}
