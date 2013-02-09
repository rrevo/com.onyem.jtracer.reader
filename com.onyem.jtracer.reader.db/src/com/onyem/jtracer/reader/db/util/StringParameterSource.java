package com.onyem.jtracer.reader.db.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.onyem.jtracer.reader.db.ParameterSource;

public final class StringParameterSource implements ParameterSource {

  private final String value;

  public StringParameterSource(String value) {
    this.value = value;
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    statement.setString(1, value);
  }

}
