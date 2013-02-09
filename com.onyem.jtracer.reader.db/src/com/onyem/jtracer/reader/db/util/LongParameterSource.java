package com.onyem.jtracer.reader.db.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.onyem.jtracer.reader.db.ParameterSource;

public final class LongParameterSource implements ParameterSource {

  private final long value;

  public LongParameterSource(long value) {
    this.value = value;
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    statement.setLong(1, value);
  }

}
