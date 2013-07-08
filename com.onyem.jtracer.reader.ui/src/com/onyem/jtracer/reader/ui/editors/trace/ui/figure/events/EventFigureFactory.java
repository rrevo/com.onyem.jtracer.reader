package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;

import com.onyem.jtracer.reader.events.model.IExceptionCatchInvocationEvent;
import com.onyem.jtracer.reader.events.model.IExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationLoopEvent;
import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodExitThrowInvocationEvent;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.IClassTraceChecker;

public class EventFigureFactory {

  private final EventFigureServices services;
  private final IClassTraceChecker classTraceChecker;

  public EventFigureFactory(IMetaService metaService,
      IClassTraceChecker classTraceChecker) {
    services = new EventFigureServices(Activator.getImageManager(), metaService);
    this.classTraceChecker = classTraceChecker;
  }

  public InvocationEventFigure create(IInvocationEvent invocationEvent,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure,
      ConnectionLayer connectionsLayer) {
    switch (invocationEvent.getType()) {
    case MethodEntry:
      return new MethodEntryInvocationFigure(services,
          (IMethodEntryInvocationEvent) invocationEvent, previousEventFigure,
          previousThreadFigure);

    case MethodExit:
      return new MethodExitInvocationFigure(services,
          (IMethodExitInvocationEvent) invocationEvent, previousEventFigure,
          previousThreadFigure);

    case MethodThrowExit:
      return new MethodExitThrowInvocationFigure(services,
          (IMethodExitThrowInvocationEvent) invocationEvent,
          previousEventFigure, previousThreadFigure);

    case ExceptionThrow:
      return new ExceptionThrowInvocationFigure(services,
          (IExceptionThrowInvocationEvent) invocationEvent,
          previousEventFigure, previousThreadFigure);

    case ExceptionCatch:
      return new ExceptionCatchInvocationFigure(services,
          (IExceptionCatchInvocationEvent) invocationEvent,
          previousEventFigure, previousThreadFigure, classTraceChecker);

    case Loop:
      IInvocationLoopEvent loopEvent = (IInvocationLoopEvent) invocationEvent;
      List<InvocationEventFigure> loopFigures = createLoopEventFigures(
          loopEvent, connectionsLayer);
      return new LoopInvocationFigure(services, loopEvent, loopFigures,
          previousEventFigure, previousThreadFigure, connectionsLayer);

    default:
      throw new IllegalArgumentException();
    }
  }

  private List<InvocationEventFigure> createLoopEventFigures(
      IInvocationLoopEvent loopEvent, ConnectionLayer connectionsLayer) {
    InvocationEventFigure previousStreamFigure = null;
    InvocationEventFigure previousThreadFigure = null;
    List<InvocationEventFigure> loopEventFigures = new ArrayList<InvocationEventFigure>();
    for (IInvocationEvent event : loopEvent.getEvents()) {
      InvocationEventFigure loopEventFigure = create(event,
          previousStreamFigure, previousThreadFigure, connectionsLayer);
      loopEventFigures.add(loopEventFigure);
      previousStreamFigure = loopEventFigure;
      previousThreadFigure = loopEventFigure;
    }
    return loopEventFigures;
  }
}
