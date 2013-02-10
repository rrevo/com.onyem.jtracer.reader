package com.onyem.jtracer.reader.ui.editors.trace.ui.figure;

import org.eclipse.draw2d.Label;

import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.ui.util.SWTResourceManager;

public class InvocationThreadHeaderFigure extends Label {

  private final IInvocationThread invocationThread;

  public InvocationThreadHeaderFigure(IInvocationThread invocationThread) {

    this.invocationThread = invocationThread;

    long threadId = invocationThread.getId();

    setText("Thread " + threadId);
    setFont(SWTResourceManager.getBoldFont());
  }

  @Override
  public String toString() {
    return "InvocationThreadHeaderFigure [invocationThread=" + invocationThread
        + "]";
  }

}
