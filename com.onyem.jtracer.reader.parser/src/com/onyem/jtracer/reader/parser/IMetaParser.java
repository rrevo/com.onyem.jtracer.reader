package com.onyem.jtracer.reader.parser;

import java.io.Closeable;
import java.io.IOException;

public interface IMetaParser extends Closeable {

  String getName();

  String getLine(long id);

  @Override
  public void close() throws IOException;
}
