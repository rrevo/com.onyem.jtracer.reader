package com.onyem.jtracer.reader.db.transactional;

import java.io.IOException;
import java.util.List;

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

public class UpdateTransactionTest {

  private String dbPath;
  private UpdateTestDAO dao;

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
    dao = injector.getInstance(UpdateTestDAOFactory.class).create(helper);
    dao.setup();
  }

  @After
  public void teardown() throws IOException {
    FileUtils.deleteFile(dbPath);
  }

  @Test
  public void testTransactional() {
    TestHelper.assertNotInTransaction();
    List<ConnectionInfo> infos = dao.updateTest();
    TestHelper.assertNotInTransaction();

    Assert.assertEquals(5, infos.size());
    Assert.assertEquals(false, infos.get(0).isAutoCommit());
    Assert.assertEquals(infos.get(0), infos.get(1));
    Assert.assertEquals(infos.get(0), infos.get(2));
    Assert.assertEquals(infos.get(0), infos.get(3));
    Assert.assertEquals(infos.get(0), infos.get(4));

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
    List<ConnectionInfo> infos = dao.updateNonTransactionTest();
    TestHelper.assertNotInTransaction();

    Assert.assertEquals(2, infos.size());
    Assert.assertEquals(true, infos.get(0).isAutoCommit());
    Assert.assertEquals(true, infos.get(1).isAutoCommit());

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
      dao.updateExceptionalTest();
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
