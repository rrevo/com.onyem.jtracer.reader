package com.onyem.jtracer.reader.ui.editors.trace.ui;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class SimpleFigureCanvas extends Canvas {

  private final LightweightSystem lws;

  public SimpleFigureCanvas(Composite parent) {
    super(parent, SWT.DOUBLE_BUFFERED);
    this.lws = new LightweightSystem();
    lws.setControl(this);
    lws.getRootFigure().setFont(parent.getFont());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
   */
  @Override
  public Point computeSize(int wHint, int hHint, boolean changed) {
    int borderSize = computeTrim(0, 0, 0, 0).x * -2;
    if (wHint >= 0)
      wHint = Math.max(0, wHint - borderSize);
    if (hHint >= 0)
      hHint = Math.max(0, hHint - borderSize);
    Dimension size = lws.getRootFigure().getPreferredSize(wHint, hHint)
        .getExpanded(borderSize, borderSize);
    size.union(new Dimension(wHint, hHint));
    return new Point(size.width, size.height);
  }

  public void setContents(IFigure figure) {
    lws.setContents(figure);
  }

}
