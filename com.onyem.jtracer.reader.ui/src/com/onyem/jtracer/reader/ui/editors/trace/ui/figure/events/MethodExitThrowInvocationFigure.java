package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.events.model.IMethodExitThrowInvocationEvent;
import com.onyem.jtracer.reader.ui.IImageManager;

class MethodExitThrowInvocationFigure extends MethodInvocationFigure {

  MethodExitThrowInvocationFigure(EventFigureServices services,
      IMethodExitThrowInvocationEvent methodInvocation,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    super(services, methodInvocation, previousEventFigure, previousThreadFigure);
  }

  @Override
  protected Image getImage() {
    return services.imageManager.getImage(IImageManager.EXCEPTION_THROW);
  }

  @Override
  public int getPreIndent() {
    return -1;
  }

  @Override
  public int getPostIndent() {
    return 0;
  }

}
