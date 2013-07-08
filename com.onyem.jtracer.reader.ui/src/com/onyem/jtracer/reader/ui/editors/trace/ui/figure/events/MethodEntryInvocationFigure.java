package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.events.model.IMethodEntryInvocationEvent;
import com.onyem.jtracer.reader.ui.IImageManager;

class MethodEntryInvocationFigure extends MethodInvocationFigure {

  MethodEntryInvocationFigure(EventFigureServices services,
      IMethodEntryInvocationEvent methodInvocation,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    super(services, methodInvocation, previousEventFigure, previousThreadFigure);
  }

  @Override
  protected Image getImage() {
    return services.imageManager.getImage(IImageManager.METHOD_ENTRY);
  }

  @Override
  public int getPreIndent() {
    return 1;
  }

  @Override
  public int getPostIndent() {
    return 1;
  }

}
