package com.onyem.jtracer.reader.db.internal;

import java.sql.Connection;
import java.sql.SQLException;

import com.onyem.jtracer.reader.db.RuntimeSQLException;

public class ConnectionHolder {

  private Connection connection = null;
  private boolean isTransaction = false;

  private static class ThreadLocalConnection extends
      ThreadLocal<ConnectionHolder> {

    @Override
    protected ConnectionHolder initialValue() {
      return new ConnectionHolder();
    }
  }

  private static ThreadLocalConnection conn = new ThreadLocalConnection();

  public static boolean isTransaction() {
    return conn.get().isTransaction;
  }

  public static void beginTransaction() {
    conn.get().isTransaction = true;
  }

  public static Connection getConnection() {
    return conn.get().connection;
  }

  public static void setConnection(Connection connection) {
    try {
      assert connection.getAutoCommit();
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      throw new RuntimeSQLException(e);
    }
    conn.get().connection = connection;
  }

  public static void clearConnection() {
    Connection connection = conn.get().connection;
    conn.get().connection = null;
    conn.get().isTransaction = false;
    try {
      connection.setAutoCommit(true);
      connection.close();
    } catch (SQLException e) {
      throw new RuntimeSQLException(e);
    }
  }
}