package com.onyem.jtracer.reader.meta.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.concurrent.NotThreadSafe;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.internal.ClassIdImpl;

@NotThreadSafe
class ClassIdResultRowMapper implements ResultRowMapper<ClassId> {

  ClassIdResultRowMapper() {
  }

  @Override
  public ClassId mapRow(ResultSet rs, int rowNum) throws SQLException {
    Long id = rs.getLong("ID");
    return new ClassIdImpl(id);
  }

}
