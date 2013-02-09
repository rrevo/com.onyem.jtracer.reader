package com.onyem.jtracer.reader.parser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.parser.factory.MetaParserFactory;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.utils.FileUtils;

public class MetaParserTest {

  private IMetaParser parser;
  private String name;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new ParserModule());
    MetaParserFactory parserFactory = injector
        .getInstance(MetaParserFactory.class);

    String path = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        "/test-data/meta1.txt");

    File f = new File(path);
    RandomAccessFile file = new RandomAccessFile(path, "r");
    name = f.getName();
    parser = parserFactory.create(name, file);
  }

  @After
  public void teardown() throws IOException {
    parser.close();
  }

  @Test
  public void test() {
    Assert.assertEquals(name, parser.getName());
    final int MAX = 10;
    for (int i = 0; i <= MAX; i++) {
      String line = parser.getLine(i);
      Assert.assertTrue(line.startsWith("<"));
      Assert.assertTrue(line.contains("|" + i + "|"));
      Assert.assertTrue(line.endsWith(">"));
    }
  }
}
