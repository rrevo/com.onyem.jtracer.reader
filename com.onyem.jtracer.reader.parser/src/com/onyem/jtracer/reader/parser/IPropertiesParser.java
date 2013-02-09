package com.onyem.jtracer.reader.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface IPropertiesParser extends Closeable {

  List<String> getKeys();

  boolean isKey(String key);

  String getValue(String key);

  List<String> getMultiValue(String key);

  @Override
  public void close() throws IOException;

}
