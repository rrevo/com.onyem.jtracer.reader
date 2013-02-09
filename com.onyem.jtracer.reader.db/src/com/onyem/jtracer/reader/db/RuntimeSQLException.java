package com.onyem.jtracer.reader.db;

import java.sql.SQLException;

public class RuntimeSQLException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -5829216146961686353L;

  public RuntimeSQLException(SQLException cause) {
    super(cause);
  }

}
