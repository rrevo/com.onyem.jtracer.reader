package com.onyem.jtracer.reader.ui.factory;

import com.onyem.jtracer.reader.ui.editors.trace.ui.TraceResultClient;

public interface TraceFactory {

  void create(String applicationPath, TraceResultClient traceResultClient);
}
