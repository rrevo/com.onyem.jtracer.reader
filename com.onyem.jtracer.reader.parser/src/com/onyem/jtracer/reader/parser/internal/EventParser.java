package com.onyem.jtracer.reader.parser.internal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.parser.IEventParser;
import com.onyem.jtracer.reader.parser.ILine;

@Service
@ThreadSafe
public class EventParser implements IEventParser {

  private final String name;
  private final RandomAccessFile eventFile;

  @Inject
  public EventParser(@Assisted String name, @Assisted RandomAccessFile eventFile) {
    this.name = name;
    this.eventFile = eventFile;
  }

  @Override
  public synchronized void close() throws IOException {
    eventFile.close();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public synchronized List<ILine> getLines(long startPosition, int count) {
    if (startPosition < IEventParser.START_POSITION) {
      throw new IllegalArgumentException();
    }
    List<ILine> lines = new ArrayList<ILine>();
    try {
      eventFile.seek(startPosition == IEventParser.START_POSITION ? 0
          : startPosition);

      String line = "";
      if (startPosition >= 0) {
        // Skip one line
        line = eventFile.readLine();
      }
      if (line != null) {
        for (int i = 0; i < count; i++) {
          ILine currentLine = getLine();
          if (currentLine != null) {
            lines.add(currentLine);
          } else {
            break;
          }
        }
      }
      return Collections.unmodifiableList(lines);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ILine getLine() throws IOException {
    long filePointer = eventFile.getFilePointer();
    String data = eventFile.readLine();
    if (data == null) {
      return null;
    }
    if (!data.startsWith("#")) {
      ILine line = new Line(filePointer, data);
      return line;
    }
    return getLine();
  }
}
