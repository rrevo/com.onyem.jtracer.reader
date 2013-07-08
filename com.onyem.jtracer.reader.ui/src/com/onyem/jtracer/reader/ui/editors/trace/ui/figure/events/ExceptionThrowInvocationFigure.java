package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.events.model.IExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.ui.IImageManager;

class ExceptionThrowInvocationFigure extends MethodTraceInvocationFigure {

  ExceptionThrowInvocationFigure(EventFigureServices services,
      IExceptionThrowInvocationEvent invocationEvent,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    super(services, invocationEvent, previousEventFigure, previousThreadFigure);
  }

  @Override
  protected Image getImage() {
    return services.imageManager.getImage(IImageManager.EXCEPTION_THROW);
  }

  @Override
  public int getPreIndent() {
    return 0;
  }

  @Override
  public int getPostIndent() {
    return 0;
  }

}
