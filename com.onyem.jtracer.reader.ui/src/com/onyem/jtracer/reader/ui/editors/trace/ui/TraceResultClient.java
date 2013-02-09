package com.onyem.jtracer.reader.ui.editors.trace.ui;

import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;

public interface TraceResultClient {

  public void setTraceResult(Trace trace, Exception exception);

}
