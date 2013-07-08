package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.events.model.IExceptionCatchInvocationEvent;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.ClassTraceCheckerFactory;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.IClassTraceChecker;

class ExceptionCatchInvocationFigure extends MethodTraceInvocationFigure {

  private final IClassTraceChecker classTraceChecker;

  ExceptionCatchInvocationFigure(EventFigureServices services,
      IExceptionCatchInvocationEvent invocationEvent,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure,
      IClassTraceChecker classTraceChecker) {
    super(services, invocationEvent, previousEventFigure, previousThreadFigure);

    this.classTraceChecker = classTraceChecker;

    if (!(previousThreadFigure instanceof ExceptionThrowInvocationFigure)) {
      throw new RuntimeException();
    }
  }

  @Override
  protected Image getImage() {
    return services.imageManager.getImage(IImageManager.EXCEPTION_CATCH);
  }

  @Override
  public int getPreIndent() {
    IClass clazz = services.metaService.getMethodClass(getFirstMethod());
    if (classTraceChecker.isTraced(clazz)) {
      ExceptionThrowInvocationFigure throwFigure = (ExceptionThrowInvocationFigure) previousThreadFigure;

      int traceDifference = ClassTraceCheckerFactory.getTraceDifference(
          services.metaService, classTraceChecker,
          throwFigure.methodTraceInvocation.getMethodTrace(),
          methodTraceInvocation.getMethodTrace());
      return (traceDifference * -2);
    }
    return 0;
  }

  @Override
  public int getPostIndent() {
    IClass clazz = services.metaService.getMethodClass(getFirstMethod());
    if (classTraceChecker.isTraced(clazz)) {
      return 1;
    }
    return 0;
  }

}
