package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.layout;

import java.util.HashMap;
import java.util.Map;

import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events.InvocationEventFigure;
import com.onyem.jtracer.reader.ui.util.Constants;

public class LayoutCache {
  private final Map<InvocationEventFigure, Integer> cachedY = new HashMap<InvocationEventFigure, Integer>();

  public void clear() {
    cachedY.clear();
  }

  public int getCachedY(InvocationEventFigure eventFigure) {
    // If the figure has been calculated
    if (cachedY.containsKey(eventFigure)) {
      return cachedY.get(eventFigure);
    }
    InvocationEventFigure previousStreamFigure = eventFigure
        .getPreviousStreamFigure();
    int value = -1;
    if (previousStreamFigure == null) {
      value = Constants.METHOD_STEP;
    } else {
      value = getCachedY(previousStreamFigure)
          + previousStreamFigure.getPreferredSize().height
          + Constants.METHOD_STEP;
    }
    cachedY.put(eventFigure, value);
    return value;
  }
}
