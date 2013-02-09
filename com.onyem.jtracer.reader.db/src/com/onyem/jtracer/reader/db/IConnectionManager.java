package com.onyem.jtracer.reader.db;

import java.sql.Connection;

public interface IConnectionManager {

  Connection createConnection();

  void closeDatabase();

}
