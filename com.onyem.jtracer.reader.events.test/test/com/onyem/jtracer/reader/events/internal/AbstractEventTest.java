package com.onyem.jtracer.reader.events.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.factory.DbModule;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;
import com.onyem.jtracer.reader.events.factory.EventModule;
import com.onyem.jtracer.reader.events.factory.IEventServiceFactory;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.factory.IMetaServiceFactory;
import com.onyem.jtracer.reader.meta.factory.MetaModule;
import com.onyem.jtracer.reader.parser.IEventParser;
import com.onyem.jtracer.reader.parser.IMetaParser;
import com.onyem.jtracer.reader.parser.factory.EventParserFactory;
import com.onyem.jtracer.reader.parser.factory.MetaParserFactory;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.utils.FileUtils;

public abstract class AbstractEventTest {

  protected IMetaService metaService;
  protected IEventServiceExtended eventService;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new DbModule(), new MetaModule(),
        new ParserModule(), new EventModule());

    MetaParserFactory metaParserFactory = injector
        .getInstance(MetaParserFactory.class);

    String metaPath = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        getMetaPath());
    File file = new File(metaPath);
    RandomAccessFile metaFile = new RandomAccessFile(metaPath, "r");
    IMetaParser metaParser = metaParserFactory.create(file.getName(), metaFile);

    String dbPath = FileUtils.getTempPath("onyemdb");
    IConnectionManager manager = injector.getInstance(
        IConnectionManagerFactory.class).createWithMigration(dbPath);

    IMetaServiceFactory factory = injector
        .getInstance(IMetaServiceFactory.class);
    metaService = factory.create(manager, metaParser);

    EventParserFactory eventParserFactory = injector
        .getInstance(EventParserFactory.class);

    String eventPath = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        getEventPath());
    file = new File(eventPath);
    RandomAccessFile eventFile = new RandomAccessFile(eventPath, "r");
    IEventParser eventParser = eventParserFactory.create(file.getName(),
        eventFile);

    IEventServiceFactory eventFactory = injector
        .getInstance(IEventServiceFactory.class);
    eventService = (IEventServiceExtended) eventFactory.create(manager,
        eventParser, metaService, getEventCount());
  }

  @After
  public void teardown() throws IOException {
    metaService.close();
    eventService.close();
  }

  abstract String getMetaPath();

  abstract String getEventPath();

  protected int getEventCount() {
    return IEventServiceExtended.DEFAULT_EVENTS_COUNT;
  }

}
