package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.connector;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.InvocationEventFigure;

public abstract class AbstractBoundsConnectionAnchor extends
    AbstractConnectionAnchor {

  public AbstractBoundsConnectionAnchor(IFigure owner) {
    super(owner);
  }

  protected final Rectangle getTranslatedBox() {
    Rectangle r = Rectangle.SINGLETON;
    r.setBounds(getBox());
    r.translate(-1, -1);
    r.resize(1, 1);

    getOwner().translateToAbsolute(r);
    return r;
  }

  protected abstract IFigure getConnectionFigure(
      InvocationEventFigure invocationFigure);

  protected final Rectangle getBox() {
    IFigure owner = getOwner();
    InvocationEventFigure invocationFigure = (InvocationEventFigure) owner;
    return getConnectionFigure(invocationFigure).getBounds();
  }
}
