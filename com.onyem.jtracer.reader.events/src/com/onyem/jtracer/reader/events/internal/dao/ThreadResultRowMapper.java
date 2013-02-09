package com.onyem.jtracer.reader.events.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.events.internal.InvocationThread;
import com.onyem.jtracer.reader.events.model.IInvocationThread;

@Immutable
class ThreadResultRowMapper implements ResultRowMapper<IInvocationThread> {

  @Override
  public IInvocationThread mapRow(ResultSet rs, int rowNum) throws SQLException {
    Long id = rs.getLong("ID");
    return new InvocationThread(id);
  }

}
