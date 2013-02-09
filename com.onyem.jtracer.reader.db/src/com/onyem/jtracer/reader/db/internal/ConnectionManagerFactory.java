package com.onyem.jtracer.reader.db.internal;

import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;

public class ConnectionManagerFactory implements IConnectionManagerFactory {

  @Override
  public IConnectionManager createWithMigration(String dbPath) {
    return create(dbPath, true);
  }

  @Override
  public IConnectionManager createWithoutMigration(String dbPath) {
    return create(dbPath, false);
  }

  private IConnectionManager create(String dbPath, boolean isMigration) {
    ConnectionManager manager = new ConnectionManager(dbPath);
    if (isMigration) {
      SchemaMigrator migrator = new SchemaMigrator(manager);
      migrator.migrate();
    }
    return manager;
  }
}
