package com.onyem.jtracer.reader.parser.internal;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.parser.ILine;

@Immutable
class Line implements ILine {

  private final long position;
  private final String data;

  public Line(long position, String data) {
    this.position = position;
    this.data = data;
  }

  @Override
  public long getPosition() {
    return position;
  }

  @Override
  public String getData() {
    return data;
  }

  @Override
  public String toString() {
    return "Line [position=" + position + ", data=" + data + "]";
  }
}