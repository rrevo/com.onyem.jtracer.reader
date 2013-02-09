package com.onyem.jtracer.reader.parser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.parser.factory.EventParserFactory;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.utils.FileUtils;

public class EventParserTest {

  private IEventParser parser;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new ParserModule());
    EventParserFactory parserFactory = injector
        .getInstance(EventParserFactory.class);

    String path = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        "/test-data/event1.txt");

    File f = new File(path);
    RandomAccessFile file = new RandomAccessFile(f, "r");
    String name = f.getName();
    parser = parserFactory.create(name, file);
    Assert.assertEquals(name, parser.getName());
  }

  @After
  public void teardown() throws IOException {
    parser.close();
  }

  @Test
  public void testAll() {
    final int LINE_COUNT = 10;
    List<ILine> lines = parser
        .getLines(IEventParser.START_POSITION, LINE_COUNT);
    Assert.assertEquals(LINE_COUNT, lines.size());
    for (int i = 0; i < LINE_COUNT; i++) {
      ILine line = lines.get(i);
      Assert.assertEquals("<" + i + ">", line.getData());
    }
  }

  @Test
  public void testOneAtATime() {
    final int LINE_COUNT = 10;
    ILine previousLine = null;
    for (int i = 0; i < LINE_COUNT; i++) {
      long startPosition = previousLine == null ? IEventParser.START_POSITION
          : previousLine.getPosition();
      List<ILine> lines = parser.getLines(startPosition, 1);
      Assert.assertEquals(1, lines.size());
      ILine line = lines.get(0);
      Assert.assertEquals("<" + i + ">", line.getData());
      previousLine = line;
    }
  }

  @Test
  public void testTwoAtATime() {
    final int LINE_COUNT = 10;
    ILine previousLine = null;
    for (int i = 0; i < LINE_COUNT; i = i + 2) {
      List<ILine> lines = parser.getLines(
          previousLine == null ? IEventParser.START_POSITION : previousLine
              .getPosition(), 2);
      Assert.assertEquals(2, lines.size());
      ILine line = lines.get(0);
      Assert.assertEquals("<" + i + ">", line.getData());

      line = lines.get(1);
      Assert.assertEquals("<" + (i + 1) + ">", line.getData());
      previousLine = line;
    }
  }

  @Test
  public void testTooManyLines() {
    final int LINE_COUNT = 10;
    final int REQUEST_COUNT = LINE_COUNT * 5;
    List<ILine> lines = parser.getLines(IEventParser.START_POSITION,
        REQUEST_COUNT);
    Assert.assertEquals(LINE_COUNT, lines.size());
    for (int i = 0; i < LINE_COUNT; i++) {
      ILine line = lines.get(i);
      Assert.assertEquals("<" + i + ">", line.getData());
    }
  }

}
