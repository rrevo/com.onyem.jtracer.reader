package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.onyem.jtracer.reader.events.model.IInvocationLoopEvent;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.connector.BottomLeftBoundsAnchor;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.connector.TopLeftBoundsAnchor;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout.LayoutCache;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout.SourceLayout;
import com.onyem.jtracer.reader.ui.util.Constants;

class LoopInvocationFigure extends InvocationEventFigure {

  private final InvocationEventFigure firstLoopEventFigure;
  private final InvocationEventFigure lastLoopEventFigure;

  LoopInvocationFigure(EventFigureServices services,
      IInvocationLoopEvent loopEvent, List<InvocationEventFigure> loopFigures,
      InvocationEventFigure previousEventFigure,
      InvocationEventFigure previousThreadFigure,
      ConnectionLayer connectionsLayer) {
    super(services, loopEvent, previousEventFigure, previousThreadFigure);

    GridLayout gridLayout = makeGridLayout();
    gridLayout.numColumns = 2;
    setLayoutManager(gridLayout);

    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    Label label = new Label(loopEvent.getLoopCount() + " iterations");
    gridLayout.setConstraint(label, gridData);
    add(label);

    RectangleFigure spacer = new RectangleFigure();
    spacer.setBounds(new Rectangle(0, 0, 5, 5));
    spacer.setAlpha(0);
    add(spacer);

    Figure eventsFigure = new Figure();
    SourceLayout sourceLayout = new SourceLayout(new LayoutCache());
    eventsFigure.setLayoutManager(sourceLayout);

    int threadX = 0;
    for (int i = 0; i < loopFigures.size(); i++) {
      final InvocationEventFigure eventFigure = loopFigures.get(i);

      int figureX = i == 0 ? 0
          : (eventFigure.getPreIndent() * Constants.INDENT_STEP) + threadX;

      sourceLayout.setConstraint(eventFigure, figureX);

      figureX = (eventFigure.getPostIndent() * Constants.INDENT_STEP) + figureX;
      threadX = figureX;

      eventsFigure.add(eventFigure);

      if (i > 0) {
        PolylineConnection connection = new PolylineConnection();

        connection.setSourceAnchor(new BottomLeftBoundsAnchor(loopFigures
            .get(i - 1)));
        connection.setTargetAnchor(new TopLeftBoundsAnchor(eventFigure));
        connectionsLayer.add(connection);
      }
    }

    add(eventsFigure);

    assert loopFigures.size() > 1;

    firstLoopEventFigure = loopFigures.get(0);
    lastLoopEventFigure = loopFigures.get(loopFigures.size() - 1);

    setBorder(new LineBorder(ColorConstants.black, 1, Graphics.LINE_DASH));
  }

  private GridLayout makeGridLayout() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.horizontalSpacing = 0;
    return gridLayout;
  }

  @Override
  public IFigure getTopConnectionFigure() {
    return firstLoopEventFigure.getTopConnectionFigure();
  }

  @Override
  public IFigure getBottomConnectionFigure() {
    return lastLoopEventFigure.getBottomConnectionFigure();
  }

  @Override
  public int getPreIndent() {
    return firstLoopEventFigure.getPreIndent();
  }

  @Override
  public int getPostIndent() {
    return lastLoopEventFigure.getPostIndent();
  }
}
