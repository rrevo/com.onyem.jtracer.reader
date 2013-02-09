package com.onyem.jtracer.reader.db.factory;

import com.onyem.jtracer.reader.db.IConnectionManager;

public interface IConnectionManagerFactory {

  IConnectionManager createWithoutMigration(String dbPath);

  IConnectionManager createWithMigration(String dbPath);

}
