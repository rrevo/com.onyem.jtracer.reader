package com.onyem.jtracer.reader.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterSource {

  public void setParameters(PreparedStatement statement) throws SQLException;

}
