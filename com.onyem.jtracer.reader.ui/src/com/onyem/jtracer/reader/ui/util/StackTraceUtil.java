package com.onyem.jtracer.reader.ui.util;

import javax.annotation.concurrent.Immutable;

@Immutable
public class StackTraceUtil {

  private StackTraceUtil() {
  }

  private static final String NEW_LINE = System.getProperty("line.separator");

  public static String asString(Throwable aThrowable) {
    final StringBuilder result = new StringBuilder();
    result.append(aThrowable.toString());
    result.append(NEW_LINE);

    for (StackTraceElement element : aThrowable.getStackTrace()) {
      result.append("    ");
      result.append(element);
      result.append(NEW_LINE);
    }
    return result.toString();
  }
}
