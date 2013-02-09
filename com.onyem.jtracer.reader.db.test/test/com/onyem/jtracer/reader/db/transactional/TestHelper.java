package com.onyem.jtracer.reader.db.transactional;

import java.sql.SQLException;

import org.junit.Assert;

import com.onyem.jtracer.reader.db.internal.ConnectionHolder;

class TestHelper {

  static void assertNotInTransaction() {
    Assert.assertFalse(ConnectionHolder.isTransaction());
    Assert.assertNull(ConnectionHolder.getConnection());
  }

  static void assertInTransaction() {
    Assert.assertTrue(ConnectionHolder.isTransaction());
  }

  static void assertInTransaction(ConnectionInfo info) {
    org.junit.Assert.assertTrue(ConnectionHolder.isTransaction());
    ConnectionInfo currentInfo = getConnectionInfo();
    Assert.assertEquals(info, currentInfo);
  }

  static ConnectionInfo getConnectionInfo() {
    ConnectionInfo info = new ConnectionInfo();
    try {
      info.setConnectionData(ConnectionHolder.getConnection());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return info;
  }

}
