package com.onyem.jtracer.reader.events.model;

public enum InvocationEventType {

  MethodEntry("+"), MethodExit("-"), MethodThrowExit("-t"), ExceptionThrow("et"), ExceptionCatch(
      "ec");

  private final String value;

  private InvocationEventType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
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
