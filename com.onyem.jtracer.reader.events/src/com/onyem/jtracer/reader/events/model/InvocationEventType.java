package com.onyem.jtracer.reader.events.model;

public enum InvocationEventType {

  MethodEntry("+"), MethodExit("-");

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
    throw new IllegalArgumentException();
  }
}
