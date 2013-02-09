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

public class UpdateReturnTransactionTest {

  private String dbPath;
  private UpdateReturnTestDAO dao;

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
    dao = injector.getInstance(UpdateReturnTestDAOFactory.class).create(helper);
    dao.setup();
  }

  @After
  public void teardown() throws IOException {
    FileUtils.deleteFile(dbPath);
  }

  @Test
  public void testTransactional() {
    TestHelper.assertNotInTransaction();
    List<ConnectionInfo> infos = dao.updateReturnTest();
    TestHelper.assertNotInTransaction();

    Assert.assertEquals(5, infos.size());
    Assert.assertEquals(false, infos.get(0).isAutoCommit());
    Assert.assertEquals(infos.get(0), infos.get(1));
    Assert.assertEquals(infos.get(0), infos.get(2));
    Assert.assertEquals(infos.get(0), infos.get(3));
    Assert.assertEquals(infos.get(0), infos.get(4));

    dao.assertData(1, "a", "A");
    dao.assertData(2, "b", "B");
    dao.assertData(3, "c", "C");
    dao.assertData(4, "d", "D");
    dao.assertData(5, "e", "E");
    dao.assertData(6, "f", "F");
    dao.assertData(7, "z", "Z");

    dao.assertDataCount(7);
  }

  @Test
  public void testNonTransactional() {
    TestHelper.assertNotInTransaction();
    List<ConnectionInfo> infos = dao.updateReturnNonTransactionTest();
    TestHelper.assertNotInTransaction();

    Assert.assertEquals(2, infos.size());
    Assert.assertEquals(true, infos.get(0).isAutoCommit());
    Assert.assertEquals(true, infos.get(1).isAutoCommit());

    dao.assertData(1, "a", "A");
    dao.assertData(2, "b", "B");
    dao.assertData(3, "g", "G");
    dao.assertData(4, "h", "H");

    dao.assertDataCount(4);
  }

  @Test
  public void testExceptionRollback() {
    TestHelper.assertNotInTransaction();
    try {
      dao.updateReturnExceptionalTest();
    } catch (RuntimeException e) {

      TestHelper.assertNotInTransaction();
      Assert.assertEquals("test", e.getMessage());
      dao.assertData(1, "a", "A");
      dao.assertData(2, "b", "B");
      dao.assertDataCount(2);
      return;
    }
    Assert.fail();
  }

}
