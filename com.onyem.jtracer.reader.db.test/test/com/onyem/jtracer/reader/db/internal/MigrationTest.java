package com.onyem.jtracer.reader.db.internal;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.factory.DbModule;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;
import com.onyem.jtracer.reader.db.transactional.TransactionalTestModule;
import com.onyem.jtracer.reader.utils.FileUtils;

public class MigrationTest {

  private String dbPath;
  private SchemaMigrator migrator;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new DbModule(),
        new TransactionalTestModule());

    dbPath = FileUtils.getTempPath("onyemdb");
    IConnectionManagerFactory f = injector
        .getInstance(IConnectionManagerFactory.class);

    IConnectionManager manager = f.createWithoutMigration(dbPath);
    migrator = new SchemaMigrator(manager);
  }

  @After
  public void teardown() throws IOException {
    FileUtils.deleteFile(dbPath);
  }

  @Test
  public void migrateAll() throws Exception {
    migrator.migrate();

    List<String> dbVersions = migrator.getDbVersions();
    String currentVersion = migrator.getCurrentVersion();
    Assert.assertEquals(dbVersions.get(dbVersions.size() - 1), currentVersion);
  }

  @Test
  public void migrateOne() throws Exception {
    List<String> dbVersions = migrator.getDbVersions();
    Assert.assertTrue(migrator.isNewDatabase());

    for (String dbVersion : dbVersions) {
      migrator.migrate(dbVersion);
      Assert.assertEquals(dbVersion, migrator.getCurrentVersion());
      Assert.assertFalse(migrator.isNewDatabase());
    }
  }
}
