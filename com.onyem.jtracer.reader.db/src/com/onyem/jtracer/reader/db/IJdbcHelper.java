package com.onyem.jtracer.reader.db;

import java.util.List;

public interface IJdbcHelper {

  String queryForString(String query);

  <T> List<T> query(String query, ResultRowMapper<T> rowMapper);

  <T> List<T> query(String query, ParameterSource parameterSource,
      ResultRowMapper<T> rowMapper);

  <T> T queryForObject(String query, ParameterSource parameterSource,
      ResultRowMapper<T> rowMapper);

  boolean execute(String sql);

  <T> T update(PreparedStatementCreator creator, ResultRowMapper<T> rowMapper);

  int update(PreparedStatementCreator creator);

}
