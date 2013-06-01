package com.onyem.jtracer.reader.ui.editors.trace.ui.figure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.onyem.jtracer.reader.events.IEventService;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;
import com.onyem.jtracer.reader.ui.editors.trace.model.figure.EventTraceFigureModel;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.connector.BottomLeftBoundsAnchor;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.connector.TopLeftBoundsAnchor;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.EventFigureFactory;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.InvocationEventFigure;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout.LayoutCache;
import com.onyem.jtracer.reader.ui.util.Constants;
import com.onyem.jtracer.reader.ui.util.Messages;
import com.onyem.jtracer.reader.ui.util.SWTUtils;

public class EventTraceFigure extends Figure implements Observer {

  private final EventTraceFigureModel model;

  private final IEventService eventService;
  private final IQueueService queueService;

  private final EventFigureFactory eventFigureFactory;
  private final LayoutCache layoutCache;

  // Child figures
  private final IFigure threadsHeaderFigure;
  private final Map<IInvocationThread, InvocationThreadHeaderFigure> threadHeaderFigureMap;

  private final IFigure threadsLayer;
  private final GridLayout threadsLayerLayout;
  private final Map<IInvocationThread, InvocationThreadFigure> threadFigureMap;

  private final ConnectionLayer connectionsLayer;

  // Last InvocationEventFigure in event
  private InvocationEventFigure lastEventFigure = null;

  // Load image state
  private Button loadImage;
  private int loadCount = 0;

  public EventTraceFigure(Trace trace, String eventFileName,
      IQueueService queueService) {

    model = new EventTraceFigureModel();
    model.addObserver(this);

    eventService = trace.getEventService(eventFileName);
    this.queueService = queueService;

    eventFigureFactory = new EventFigureFactory(trace.getClassTraceChecker());
    layoutCache = new LayoutCache();

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    setLayoutManager(gridLayout);

    // Header
    {
      ScrollPane scrollPane = new ScrollPane();
      scrollPane.setVerticalScrollBarVisibility(ScrollPane.NEVER);
      scrollPane.setHorizontalScrollBarVisibility(ScrollPane.NEVER);

      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridLayout.setConstraint(scrollPane, gridData);
      add(scrollPane);

      threadsHeaderFigure = new Figure();
      scrollPane.setContents(threadsHeaderFigure);

      XYLayout layout = new XYLayout();
      threadsHeaderFigure.setLayoutManager(layout);

      threadHeaderFigureMap = new HashMap<IInvocationThread, InvocationThreadHeaderFigure>();
    }

    // Body
    {
      final ScrollPane scrollPane = new ScrollPane();
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      gridData.horizontalAlignment = SWT.BEGINNING;
      gridData.verticalAlignment = SWT.BEGINNING;
      gridLayout.setConstraint(scrollPane, gridData);
      add(scrollPane);

      LayeredPane mainLayer = new LayeredPane();
      scrollPane.setContents(mainLayer);

      connectionsLayer = new ConnectionLayer();
      connectionsLayer.setConnectionRouter(new ManhattanConnectionRouter());
      mainLayer.add(connectionsLayer, "connections");

      threadsLayer = new LayeredPane();
      mainLayer.add(threadsLayer, "events");

      threadsLayerLayout = new GridLayout();
      threadsLayerLayout.numColumns = 0;
      threadsLayer.setLayoutManager(threadsLayerLayout);

      threadFigureMap = new HashMap<IInvocationThread, InvocationThreadFigure>();

      // Transpose the x coordinate of the threadFigure to the threadHeaderFigure
      scrollPane.addLayoutListener(new LayoutListener() {

        @Override
        public void setConstraint(IFigure child, Object constraint) {
        }

        @Override
        public void remove(IFigure child) {
        }

        @Override
        public void postLayout(IFigure container) {
          for (IInvocationThread thread : threadFigureMap.keySet()) {
            InvocationThreadFigure threadFigure = threadFigureMap.get(thread);
            InvocationThreadHeaderFigure threadHeaderFigure = threadHeaderFigureMap
                .get(thread);
            XYLayout layoutManager = (XYLayout) threadsHeaderFigure
                .getLayoutManager();
            Rectangle constraint = (Rectangle) layoutManager
                .getConstraint(threadHeaderFigure);
            int newX = threadFigure.getLocation().x;
            threadsHeaderFigure.setConstraint(threadHeaderFigure,
                new Rectangle(newX, constraint.y, constraint.width,
                    constraint.height));
          }
        }

        @Override
        public boolean layout(IFigure container) {
          return false;
        }

        @Override
        public void invalidate(IFigure container) {
        }
      });

      {
        final Layer loadImagesLayer = new LayeredPane();
        mainLayer.add(loadImagesLayer, "loadImages");

        final XYLayout xyLayout = new XYLayout();
        loadImagesLayer.setLayoutManager(xyLayout);

        loadImage = new Button(Messages.LOAD_MORE_LABEL);
        loadImagesLayer.add(loadImage);

        loadImage.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent event) {
            loadImage.setVisible(false);
            loadEvents(lastEventFigure.getInvocationEvent());
          }
        });

        loadImage.setVisible(false);

        // Make the load image float over the scrollPane 
        scrollPane.addLayoutListener(new LayoutListener() {

          @Override
          public void setConstraint(IFigure child, Object constraint) {
          }

          @Override
          public void remove(IFigure child) {
          }

          @Override
          public void postLayout(IFigure container) {
            Dimension loadImageSize = loadImage.getPreferredSize();
            Viewport viewport = scrollPane.getViewport();
            final Rectangle clientArea = EventTraceFigure.this.getParent()
                .getClientArea();

            final int clientAreaWidth = clientArea.width;
            final int clientAreaHeight = clientArea.height;

            RangeModel horizontalModel = viewport.getHorizontalRangeModel();
            int xValue = -1;
            if (horizontalModel.getMaximum() > clientAreaWidth) {
              xValue = horizontalModel.getValue() + horizontalModel.getExtent()
                  - loadImageSize.width - 10;
            } else {
              xValue = clientAreaWidth - loadImageSize.width - 30;
            }
            RangeModel verticalModel = viewport.getVerticalRangeModel();
            if (verticalModel.getMaximum() > clientAreaHeight) {
              int yValue = verticalModel.getValue() + verticalModel.getExtent()
                  - loadImageSize.height - 10;
              xyLayout.setConstraint(loadImage, new Rectangle(new Point(xValue,
                  yValue), loadImageSize));
              loadImagesLayer.invalidate();
            }
          }

          @Override
          public boolean layout(IFigure container) {
            return false;
          }

          @Override
          public void invalidate(IFigure container) {
          }
        });
      }
    }

    // Start loading events
    loadEvents(null);
  }

  private void loadEvents(final IInvocationEvent startEvent) {
    SWTUtils.assertDisplayThread();
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        model.addEvents(eventService.getNextEvent(startEvent));
      }
    };
    queueService.queueNow(runnable);
  }

  @Override
  public void update(Observable o, final Object arg) {
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      @SuppressWarnings("unchecked")
      public void run() {
        render((List<IInvocationEvent>) arg);
      }
    });
  }

  private void render(List<IInvocationEvent> newEvents) {
    SWTUtils.assertDisplayThread();

    for (IInvocationEvent invocationEvent : newEvents) {
      addEvent(invocationEvent);
    }

    // Update load count
    loadCount += newEvents.size();

    // Have all the events been loaded?
    if (!newEvents.isEmpty()) {

      // Stop loading if the threshold has been reached
      if (loadCount >= Constants.LOAD_IMAGE_THRESHOLD) {
        loadCount -= Constants.LOAD_IMAGE_THRESHOLD;
        loadImage.setVisible(true);
      } else {
        // Continue loading
        loadEvents(newEvents.get(newEvents.size() - 1));
      }
    }
  }

  private void addEvent(IInvocationEvent invocationEvent) {
    InvocationThreadFigure threadFigure = getThreadFigure(invocationEvent
        .getThread());

    InvocationEventFigure lastThreadEventFigure = threadFigure
        .getLastEventThreadFigure();
    InvocationEventFigure eventFigure = eventFigureFactory.create(
        invocationEvent, lastEventFigure, lastThreadEventFigure,
        connectionsLayer);
    threadFigure.addThreadEvent(eventFigure);

    lastEventFigure = eventFigure;

    if (lastThreadEventFigure != null) {
      PolylineConnection connection = new PolylineConnection();
      connection.setSourceAnchor(new BottomLeftBoundsAnchor(
          lastThreadEventFigure));
      connection.setTargetAnchor(new TopLeftBoundsAnchor(eventFigure));
      connectionsLayer.add(connection);
    }
  }

  private InvocationThreadFigure getThreadFigure(IInvocationThread thread) {
    if (!threadFigureMap.containsKey(thread)) {
      InvocationThreadHeaderFigure headerFigure = new InvocationThreadHeaderFigure(
          thread);
      threadHeaderFigureMap.put(thread, headerFigure);

      threadsHeaderFigure.add(headerFigure);
      threadsHeaderFigure.setConstraint(headerFigure, new Rectangle(
          new Rectangle(new Point(0, 0), headerFigure.getPreferredSize())));

      InvocationThreadFigure threadFigure = new InvocationThreadFigure(
          layoutCache, thread);
      threadsLayerLayout.numColumns = threadsLayerLayout.numColumns + 1;
      threadFigureMap.put(thread, threadFigure);

      threadsLayer.add(threadFigure);
      GridData gridData = new GridData();
      gridData.horizontalAlignment = SWT.BEGINNING;
      gridData.verticalAlignment = SWT.BEGINNING;
      threadsLayerLayout.setConstraint(threadFigure, gridData);
    }

    return threadFigureMap.get(thread);
  }
}
