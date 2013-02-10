package com.onyem.jtracer.reader.ui.editors.trace.ui.figure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.onyem.jtracer.reader.events.IEventService;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;
import com.onyem.jtracer.reader.ui.editors.trace.model.figure.EventTraceFigureModel;
import com.onyem.jtracer.reader.ui.util.SWTUtils;

public class EventTraceFigure extends Figure implements Observer {

  private final EventTraceFigureModel model;

  private final IEventService eventService;
  private final IQueueService queueService;

  // 
  private final IFigure threadsLayer;
  private final GridLayout threadsLayerLayout;
  private final Map<IInvocationThread, InvocationThreadFigure> threadFigures;

  public EventTraceFigure(Trace trace, String eventFileName,
      IQueueService queueService) {

    model = new EventTraceFigureModel();
    model.addObserver(this);

    eventService = trace.getEventService(eventFileName);
    this.queueService = queueService;

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    setLayoutManager(gridLayout);

    ScrollPane scrollPane = new ScrollPane();
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = SWT.BEGINNING;
    gridData.verticalAlignment = SWT.BEGINNING;
    gridLayout.setConstraint(scrollPane, gridData);
    add(scrollPane);

    LayeredPane mainLayer = new LayeredPane();
    scrollPane.setContents(mainLayer);

    threadsLayer = new LayeredPane();
    mainLayer.add(threadsLayer, "events");

    threadsLayerLayout = new GridLayout();
    threadsLayerLayout.numColumns = 0;
    threadsLayer.setLayoutManager(threadsLayerLayout);

    threadFigures = new HashMap<IInvocationThread, InvocationThreadFigure>();

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
      addEvent(new EventFigure(invocationEvent));
    }

    // Continue loading
    if (!newEvents.isEmpty()) {
      loadEvents(newEvents.get(newEvents.size() - 1));
    }
  }

  private void addEvent(EventFigure eventFigure) {
    InvocationThreadFigure threadFigure = getThreadFigure(eventFigure
        .getEvent().getThread());
    threadFigure.addThreadEvent(eventFigure);
  }

  private InvocationThreadFigure getThreadFigure(IInvocationThread thread) {
    if (!threadFigures.containsKey(thread)) {
      InvocationThreadFigure threadFigure = new InvocationThreadFigure(thread);
      threadsLayerLayout.numColumns = threadsLayerLayout.numColumns + 1;
      threadFigures.put(thread, threadFigure);

      threadsLayer.add(threadFigure);
      GridData gridData = new GridData();
      gridData.horizontalAlignment = SWT.BEGINNING;
      gridData.verticalAlignment = SWT.BEGINNING;
      threadsLayerLayout.setConstraint(threadFigure, gridData);
    }

    return threadFigures.get(thread);
  }

  private static class EventFigure extends Figure {

    private final IInvocationEvent invocationEvent;

    public EventFigure(IInvocationEvent invocationEvent) {
      this.invocationEvent = invocationEvent;

      GridLayout gridLayout = makeGridLayout();
      gridLayout.numColumns = 2;
      setLayoutManager(gridLayout);

      Image leftImage = getImage();
      IFigure imageFigure = new Label(leftImage);
      add(imageFigure);

      GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
      gridLayout.setConstraint(imageFigure, gridData);

      IFigure normalFigure = makeNormalFigure();
      add(normalFigure);
    }

    IInvocationEvent getEvent() {
      return invocationEvent;
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
      IFigure figure = new Label(invocationEvent.getThread().getId() + ">"
          + invocationEvent.getId());
      figure.setOpaque(true);
      return figure;
    }

    private Image getImage() {
      return Activator.getImageManager().getImage(IImageManager.METHOD_ENTRY);
    }

  }
}
