package com.onyem.jtracer.reader.events.model;

public enum InvocationEventType {

  MethodEntry("+", true), MethodExit("-", true), MethodThrowExit("-t", true), ExceptionThrow(
      "et", true), ExceptionCatch("ec", true), Loop("lo", false);

  public static int NULL = -1;

  private final String value;
  private final boolean existsInTrace;

  private InvocationEventType(String value, boolean existsInTrace) {
    this.value = value;
    this.existsInTrace = existsInTrace;
  }

  public String getValue() {
    return value;
  }

  public boolean isExistsInTrace() {
    return existsInTrace;
  }

  public static InvocationEventType parseString(String value) {
    if (value.equals(MethodEntry.value)) {
      return MethodEntry;
    }
    if (value.equals(MethodExit.value)) {
      return MethodExit;
    }
    if (value.equals(MethodThrowExit.value)) {
      return MethodThrowExit;
    }
    if (value.equals(ExceptionThrow.value)) {
      return ExceptionThrow;
    }
    if (value.equals(ExceptionCatch.value)) {
      return ExceptionCatch;
    }
    throw new IllegalArgumentException();
  }
}
