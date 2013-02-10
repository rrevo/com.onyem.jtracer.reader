package com.onyem.jtracer.reader.ui.editors.trace.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;

import com.onyem.jtracer.reader.events.model.IInvocationThread;

public class InvocationThreadFigure extends Figure {

  private final IInvocationThread invocationThread;

  public InvocationThreadFigure(/* LayoutCache layoutCache, */
  IInvocationThread invocationThread) {

    this.invocationThread = invocationThread;

    ToolbarLayout toolbarLayout = new ToolbarLayout();
    toolbarLayout.setSpacing(5);
    setLayoutManager(toolbarLayout);
  }

  public IInvocationThread getInvocationThread() {
    return invocationThread;
  }

  public void addThreadEvent(IFigure figure) {
    add(figure);
  }

  @Override
  public String toString() {
    return "InvocationThreadFigure [invocationThread=" + invocationThread + "]";
  }
}
