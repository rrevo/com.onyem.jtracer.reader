package com.onyem.jtracer.reader.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.concurrent.ThreadSafe;

import com.onyem.jtracer.reader.db.ResultRowMapper;

@ThreadSafe
public final class LongResultRowMapper implements ResultRowMapper<Long> {

  @Override
  public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
    return rs.getLong(1);
  }

}
