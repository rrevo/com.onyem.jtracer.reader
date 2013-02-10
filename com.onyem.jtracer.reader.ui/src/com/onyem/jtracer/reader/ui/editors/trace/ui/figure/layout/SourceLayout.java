package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.InvocationEventFigure;

public class SourceLayout extends AbstractLayout {

  private final LayoutCache layoutCache;
  private final Map<InvocationEventFigure, Integer> constraints = new HashMap<InvocationEventFigure, Integer>();

  public SourceLayout(LayoutCache layoutCache) {
    this.layoutCache = layoutCache;
  }

  @Override
  protected Dimension calculatePreferredSize(IFigure f, int wHint, int hHint) {
    Rectangle rect = new Rectangle();
    @SuppressWarnings("rawtypes")
    Iterator children = f.getChildren().listIterator();
    while (children.hasNext()) {
      IFigure child = (IFigure) children.next();
      Rectangle r = (Rectangle) getConstraintAsRectangle(child);
      if (r == null)
        continue;

      Dimension preferredSize = child.getPreferredSize(r.width, r.height);
      r.width = preferredSize.width;
      r.height = preferredSize.height;
      rect.union(r);
    }
    Dimension d = rect.getSize();
    Insets insets = f.getInsets();

    Dimension preferredSize = new Dimension(d.width + insets.getWidth(),
        d.height + insets.getHeight()).union(getBorderPreferredSize(f));

    return preferredSize;
  }

  @Override
  public Object getConstraint(IFigure figure) {
    return constraints.get(figure);
  }

  private Rectangle getConstraintAsRectangle(IFigure figure) {
    InvocationEventFigure eventFigure = (InvocationEventFigure) figure;
    Integer x = constraints.get(eventFigure);
    if (x == null) {
      return null;
    }
    Rectangle r = new Rectangle(x, getComputedY(eventFigure), -1, -1);
    return r;
  }

  @Override
  public void invalidate() {
    super.invalidate();
    layoutCache.clear();
  }

  private int getComputedY(InvocationEventFigure eventFigure) {
    return layoutCache.getCachedY(eventFigure);
  }

  private Point getOrigin(IFigure parent) {
    return parent.getClientArea().getLocation();
  }

  @Override
  public void layout(IFigure parent) {
    @SuppressWarnings("rawtypes")
    Iterator children = parent.getChildren().iterator();
    Point offset = getOrigin(parent);
    Point offsetNoY = new Point(offset.x, offset.y);
    IFigure f;
    while (children.hasNext()) {
      f = (IFigure) children.next();
      Rectangle bounds = (Rectangle) getConstraintAsRectangle(f);
      if (bounds == null)
        continue;

      Dimension preferredSize = f.getPreferredSize(bounds.width, bounds.height);
      bounds.width = preferredSize.width;
      bounds.height = preferredSize.height;
      bounds = bounds.getTranslated(offsetNoY);
      f.setBounds(bounds);
    }
  }

  @Override
  public void remove(IFigure figure) {
    super.remove(figure);
    constraints.remove(figure);
  }

  public void setConstraint(InvocationEventFigure figure, Object newConstraint) {
    super.setConstraint(figure, newConstraint);
    if (newConstraint != null)
      constraints.put(figure, (Integer) newConstraint);
  }
}
