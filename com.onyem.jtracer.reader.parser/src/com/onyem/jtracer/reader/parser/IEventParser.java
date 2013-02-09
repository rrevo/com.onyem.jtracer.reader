package com.onyem.jtracer.reader.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface IEventParser extends Closeable {

  long START_POSITION = -1l;

  String getName();

  /**
   * Get the next lines after the startPosition. Next line means that the line
   * at startPosition is ignored. startPosition can be any position within a
   * line.
   * count number of ILines are returned.
   * 
   * @param startPosition
   * @param count
   * @return
   */
  List<ILine> getLines(long startPosition, int count);

  @Override
  public void close() throws IOException;

}
