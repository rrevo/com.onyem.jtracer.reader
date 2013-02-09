package com.onyem.jtracer.reader.db.internal;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.concurrent.Immutable;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.db.CannotGetJdbcConnectionException;
import com.onyem.jtracer.reader.db.IConnectionManager;

@Service
@Immutable
class ConnectionManager implements IConnectionManager {

  private final String driver = "org.h2.Driver";
  private final DataSource dataSource;

  ConnectionManager(String dbPath) {
    loadDriver();

    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName(driver);
    dataSource.setDriverClassLoader(getClass().getClassLoader());
    dataSource.setUrl("jdbc:h2:" + dbPath + "/onyem");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    dataSource.setDefaultAutoCommit(true);
    this.dataSource = dataSource;
  }

  private void loadDriver() {
    try {
      Class.forName(driver).newInstance();
    } catch (InstantiationException e) {
      throw new CannotGetJdbcConnectionException(e);
    } catch (IllegalAccessException e) {
      throw new CannotGetJdbcConnectionException(e);
    } catch (ClassNotFoundException e) {
      throw new CannotGetJdbcConnectionException(e);
    }
  }

  @Override
  public synchronized Connection createConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new CannotGetJdbcConnectionException(e);
    }
  }

  @Override
  public void closeDatabase() {
    // Database is closed when all connections are closed
  }

}
