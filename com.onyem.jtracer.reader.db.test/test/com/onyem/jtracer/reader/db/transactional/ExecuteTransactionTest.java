package com.onyem.jtracer.reader.db.transactional;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.factory.DbModule;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;
import com.onyem.jtracer.reader.db.factory.IJdbcHelperFactory;
import com.onyem.jtracer.reader.utils.FileUtils;

public class ExecuteTransactionTest {

  private String dbPath;
  private ExecuteTestDAO dao;

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new DbModule(),
        new TransactionalTestModule());

    dbPath = FileUtils.getTempPath("onyemdb");
    IConnectionManagerFactory f = injector
        .getInstance(IConnectionManagerFactory.class);

    IConnectionManager manager = f.createWithoutMigration(dbPath);
    IJdbcHelper helper = injector.getInstance(IJdbcHelperFactory.class).create(
        manager);
    dao = injector.getInstance(ExecuteTestDAOFactory.class).create(helper);
    dao.setup();
  }

  @After
  public void teardown() throws IOException {
    FileUtils.deleteFile(dbPath);
  }

  @Test
  public void testTransactional() {
    TestHelper.assertNotInTransaction();
    dao.executeTest();
    TestHelper.assertNotInTransaction();

    dao.assertData("a", "A");
    dao.assertData("b", "B");
    dao.assertData("c", "B");
    dao.assertData("d", "D");
    dao.assertData("e", "E");
    dao.assertData("f", "F");
    dao.assertData("z", "Z");

    dao.assertDataCount(7);
  }

  @Test
  public void testNonTransactional() {
    TestHelper.assertNotInTransaction();
    dao.executeNonTransactionTest();
    TestHelper.assertNotInTransaction();

    dao.assertData("a", "A");
    dao.assertData("b", "B");
    dao.assertData("g", "G");
    dao.assertData("h", "H");

    dao.assertDataCount(4);
  }

  @Test
  public void testExceptionRollback() {
    TestHelper.assertNotInTransaction();
    try {
      dao.executeExceptionalTest();
    } catch (RuntimeException e) {

      TestHelper.assertNotInTransaction();
      Assert.assertEquals("test", e.getMessage());
      dao.assertData("a", "A");
      dao.assertData("b", "B");
      dao.assertDataCount(2);
      return;
    }
    Assert.fail();
  }

}
