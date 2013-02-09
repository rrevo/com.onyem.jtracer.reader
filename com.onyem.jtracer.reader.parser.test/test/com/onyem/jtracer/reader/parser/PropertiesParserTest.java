package com.onyem.jtracer.reader.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.parser.factory.PropertiesParserFactory;
import com.onyem.jtracer.reader.utils.FileUtils;

public class PropertiesParserTest {

  private IPropertiesParser parser;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new ParserModule());
    PropertiesParserFactory parserFactory = injector
        .getInstance(PropertiesParserFactory.class);

    String path = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        "/test-data/jtrace-properties");
    File file = new File(path);
    parser = parserFactory.create(file);
  }

  @After
  public void teardown() throws IOException {
    parser.close();
  }

  @Test
  public void testSingle() throws Exception {
    Assert.assertEquals("4.0.0.groovy", parser.getValue(PropertyKeys.VERSION));
    Assert.assertEquals("12345", parser.getValue(PropertyKeys.START_TIME));
    Assert.assertEquals("+org.**", parser.getValue(PropertyKeys.SELECTOR));
  }

  @Test
  public void testKeyNonExistence() {
    final String key = "fake";
    Assert.assertFalse(parser.isKey("#Comment"));
    Assert.assertFalse(parser.isKey(key));
    Assert.assertNull(parser.getValue(key));
    Assert.assertEquals(Collections.EMPTY_LIST, parser.getMultiValue(key));
  }

  @Test
  public void testMultiValues() {
    List<String> values = parser.getMultiValue(PropertyKeys.EVENT_FILE);
    Assert.assertEquals(4, values.size());
    int index = 0;
    Assert.assertEquals("a", values.get(index++));
    Assert.assertEquals("a", values.get(index++));
    Assert.assertEquals("b", values.get(index++));
    Assert.assertEquals("c", values.get(index++));
  }

  @Test
  public void testKeyExistence() {
    final String key = "random";
    Assert.assertTrue(parser.isKey(key));
    Assert.assertNull(parser.getValue(key));
  }

  @Test
  public void testKeyEmptyValue() {
    final String key = "randomWithEmptyValue";
    Assert.assertTrue(parser.isKey(key));
    Assert.assertNull("", parser.getValue(key));
  }
}
