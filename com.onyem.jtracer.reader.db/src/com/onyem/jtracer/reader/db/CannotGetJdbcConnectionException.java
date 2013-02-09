package com.onyem.jtracer.reader.db;

public class CannotGetJdbcConnectionException extends RuntimeException {

  private static final long serialVersionUID = 3241404338851260479L;

  public CannotGetJdbcConnectionException(Exception e) {
    super(e);
  }

}
