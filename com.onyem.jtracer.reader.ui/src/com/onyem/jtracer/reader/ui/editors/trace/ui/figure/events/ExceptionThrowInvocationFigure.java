package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.events.model.IExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.ui.IImageManager;

class ExceptionThrowInvocationFigure extends MethodTraceInvocationFigure {

  ExceptionThrowInvocationFigure(IImageManager imageManager,
      IExceptionThrowInvocationEvent invocationEvent,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    super(imageManager, invocationEvent, previousEventFigure,
        previousThreadFigure);
  }

  @Override
  protected Image getImage() {
    return imageManager.getImage(IImageManager.EXCEPTION_THROW);
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
