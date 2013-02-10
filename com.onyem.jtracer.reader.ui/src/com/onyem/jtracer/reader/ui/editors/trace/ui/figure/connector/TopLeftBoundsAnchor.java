package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.connector;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.InvocationEventFigure;

public class TopLeftBoundsAnchor extends AbstractBoundsConnectionAnchor {

  public TopLeftBoundsAnchor(IFigure owner) {
    super(owner);
  }

  @Override
  public Point getLocation(Point reference) {
    Rectangle r = getTranslatedBox();
    Point center = r.getCenter();
    return new Point(center.x, center.y - (IImageManager.IMAGE_SIZE / 2) + 2);
  }

  @Override
  protected IFigure getConnectionFigure(InvocationEventFigure invocationFigure) {
    return invocationFigure.getTopConnectionFigure();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TopLeftBoundsAnchor) {
      TopLeftBoundsAnchor other = (TopLeftBoundsAnchor) obj;
      return other.getOwner() == getOwner() && other.getBox().equals(getBox());
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (getOwner() != null)
      return getOwner().hashCode();
    else
      return super.hashCode();
  }
}
