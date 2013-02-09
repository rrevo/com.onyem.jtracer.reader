package com.onyem.jtracer.reader.db.transactional;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionInfo {

  private int connectionHashCode;
  private boolean autoCommit;

  public void setConnectionData(Connection connection) throws SQLException {
    connectionHashCode = connection.hashCode();
    autoCommit = connection.getAutoCommit();
  }

  public int getConnectionHashCode() {
    return connectionHashCode;
  }

  public boolean isAutoCommit() {
    return autoCommit;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (autoCommit ? 1231 : 1237);
    result = prime * result + connectionHashCode;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConnectionInfo other = (ConnectionInfo) obj;
    if (autoCommit != other.autoCommit) {
      return false;
    }
    if (connectionHashCode != other.connectionHashCode) {
      return false;
    }
    return true;
  }

}
