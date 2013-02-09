package com.onyem.jtracer.reader.db.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.ResultRowMapper;

class SchemaMigrator {

  private final JdbcHelper q;
  private final List<String> dbVersions;

  SchemaMigrator(IConnectionManager connectionManager) {
    q = new JdbcHelper(connectionManager);

    List<String> versions = new ArrayList<String>();
    versions.add("0.1.0");
    versions.add("0.2.0");
    versions.add("0.3.0");
    dbVersions = Collections.unmodifiableList(versions);
  }

  public void migrate() {
    // Migrate from 0th version
    int versionIndex = -1;
    if (!isNewDatabase()) {
      // Migrate from current version
      String currentVersion = getCurrentVersion();
      versionIndex = dbVersions.indexOf(currentVersion);
    }
    while ((versionIndex + 1) < dbVersions.size()) {
      try {
        migrate(dbVersions.get(versionIndex + 1));
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      versionIndex++;
    }
  }

  List<String> getDbVersions() {
    return dbVersions;
  }

  boolean isNewDatabase() {
    List<Object> results = q
        .query(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?",
            new ParameterSource() {

              @Override
              public void setParameters(PreparedStatement statement)
                  throws SQLException {
                statement.setString(1, "ONYEM");
              }
            }, new ResultRowMapper<Object>() {

              @Override
              public Object mapRow(ResultSet rs, int rowNum)
                  throws SQLException {
                return rs.getObject(1);
              }
            });
    return results.isEmpty();
  }

  String getCurrentVersion() {
    return q.queryForString("SELECT VERSION FROM ONYEM");
  }

  void migrate(String dbVersion) throws IOException, SQLException {
    URL url = Platform.getBundle(Constants.PLUGIN_ID).getEntry(
        "/schemas/migrations/" + dbVersion + ".sql");
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        url.openStream()));
    try {
      String line = reader.readLine();
      StringBuffer buffer = new StringBuffer();
      while (line != null) {
        line = line.trim();
        if (!line.startsWith("--")) {
          buffer.append(line);
          if (line.endsWith(";")) {
            buffer.setCharAt(buffer.length() - 1, ' ');
            String query = buffer.toString();
            q.execute(query);
            buffer.setLength(0);
          }
        }
        line = reader.readLine();
      }
    } finally {
      reader.close();
    }
  }
}
