package com.onyem.jtracer.reader.meta;

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
import com.onyem.jtracer.reader.meta.factory.IMetaServiceFactory;
import com.onyem.jtracer.reader.meta.factory.MetaModule;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;
import com.onyem.jtracer.reader.parser.IMetaParser;
import com.onyem.jtracer.reader.parser.factory.MetaParserFactory;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.utils.FileUtils;

public abstract class AbstractMetaTest {

  protected IMetaServiceExtended metaService;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new DbModule(), new MetaModule(),
        new ParserModule());
    MetaParserFactory parserFactory = injector
        .getInstance(MetaParserFactory.class);

    String path = FileUtils.getPluginPath(Constants.PLUGIN_ID, getMetaPath());

    File f = new File(path);
    RandomAccessFile file = new RandomAccessFile(path, "r");
    IMetaParser parser = parserFactory.create(f.getName(), file);

    String dbPath = FileUtils.getTempPath("onyemdb");
    IConnectionManager manager = injector.getInstance(
        IConnectionManagerFactory.class).createWithMigration(dbPath);

    IMetaServiceFactory factory = injector
        .getInstance(IMetaServiceFactory.class);
    metaService = (IMetaServiceExtended) factory.create(manager, parser);
  }

  protected abstract String getMetaPath();

  @After
  public void teardown() throws IOException {
    metaService.close();
  }
}
