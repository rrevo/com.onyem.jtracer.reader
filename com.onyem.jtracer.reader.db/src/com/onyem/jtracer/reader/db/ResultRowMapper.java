package com.onyem.jtracer.reader.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultRowMapper<T> {

  T mapRow(ResultSet rs, int rowNum) throws SQLException;

}
