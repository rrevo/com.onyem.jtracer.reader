package com.onyem.jtracer.reader.ui.editors.trace.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;

import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.InvocationEventFigure;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout.LayoutCache;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout.SourceLayout;
import com.onyem.jtracer.reader.ui.util.Constants;

public class InvocationThreadFigure extends Figure {

  private final IInvocationThread invocationThread;
  private final IFigure eventsFigure;
  private final SourceLayout sourceLayout;

  private int threadX = 0;
  private InvocationEventFigure lastEventThreadFigure;

  public InvocationThreadFigure(LayoutCache layoutCache,
      IInvocationThread invocationThread) {

    this.invocationThread = invocationThread;

    ToolbarLayout toolbarLayout = new ToolbarLayout();
    toolbarLayout.setSpacing(5);
    setLayoutManager(toolbarLayout);

    this.eventsFigure = new Figure();

    this.sourceLayout = new SourceLayout(layoutCache);
    eventsFigure.setLayoutManager(sourceLayout);

    add(eventsFigure);
  }

  public IInvocationThread getInvocationThread() {
    return invocationThread;
  }

  public InvocationEventFigure getLastEventThreadFigure() {
    return lastEventThreadFigure;
  }

  public void addThreadEvent(InvocationEventFigure figure) {

    int figureX = (figure.getPreIndent() * Constants.INDENT_STEP) + threadX;
    sourceLayout.setConstraint(figure, figureX);
    threadX = (figure.getPostIndent() * Constants.INDENT_STEP) + figureX;

    eventsFigure.add(figure);
    lastEventThreadFigure = figure;
  }

  @Override
  public String toString() {
    return "InvocationThreadFigure [invocationThread=" + invocationThread + "]";
  }
}
