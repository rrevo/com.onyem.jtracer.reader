package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;

import com.onyem.jtracer.reader.events.model.IMethodTraceInvocationEvent;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.ui.IImageManager;

abstract class MethodTraceInvocationFigure extends InvocationEventFigure {

  protected final IMethodTraceInvocationEvent methodTraceInvocation;
  private final IFigure imageFigure;
  private final IFigure normalFigure;

  MethodTraceInvocationFigure(IImageManager imageManager,
      IMethodTraceInvocationEvent methodTraceInvocation,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure) {
    super(imageManager, methodTraceInvocation, previousEventFigure,
        previousThreadFigure);

    this.methodTraceInvocation = methodTraceInvocation;

    GridLayout gridLayout = makeGridLayout();
    gridLayout.numColumns = 2;
    setLayoutManager(gridLayout);

    Image leftImage = getImage();
    imageFigure = new Label(leftImage);
    add(imageFigure);

    GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gridLayout.setConstraint(imageFigure, gridData);

    normalFigure = makeNormalFigure();
    add(normalFigure);
  }

  private GridLayout makeGridLayout() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.horizontalSpacing = 0;
    return gridLayout;
  }

  private IFigure makeNormalFigure() {
    IMethod invocationMethod = getFirstMethod();
    String methodName = invocationMethod.getName();
    IClass clazz = invocationMethod.getIClass();
    IFigure figure = new Label(clazz.getClassName() + " " + methodName);
    figure.setOpaque(true);
    return figure;
  }

  protected IMethod getFirstMethod() {
    return methodTraceInvocation.getMethodTrace().get(0);
  }

  protected abstract Image getImage();

  @Override
  public IFigure getTopConnectionFigure() {
    return imageFigure;
  }

  @Override
  public IFigure getBottomConnectionFigure() {
    return imageFigure;
  }

  @Override
  public String toString() {
    return "MethodTraceInvocationFigure [methodTraceInvocation="
        + methodTraceInvocation + "]";
  }

}
