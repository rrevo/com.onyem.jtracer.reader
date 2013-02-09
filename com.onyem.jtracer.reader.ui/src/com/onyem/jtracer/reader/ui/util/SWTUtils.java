package com.onyem.jtracer.reader.ui.util;

import javax.annotation.concurrent.Immutable;

import org.eclipse.swt.widgets.Display;

@Immutable
public final class SWTUtils {

  private SWTUtils() {
  }

  public static void assertDisplayThread() {
    assert Display.getDefault().getThread() == Thread.currentThread();
  }
}
