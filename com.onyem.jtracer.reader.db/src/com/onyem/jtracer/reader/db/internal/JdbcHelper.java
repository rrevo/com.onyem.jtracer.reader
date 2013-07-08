package com.onyem.jtracer.reader.db.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.PreparedStatementCreator;
import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.db.RuntimeSQLException;
import com.onyem.jtracer.reader.db.TransactionalAware;

@Immutable
public class JdbcHelper implements IJdbcHelper {

  private static final String NEWLINE = "\n";
  private final IConnectionManager m;
  private final NullParameterSource nullParameterSource = new NullParameterSource();

  @Inject
  JdbcHelper(@Assisted IConnectionManager connectionManager) {
    this.m = connectionManager;
  }

  @Override
  @TransactionalAware
  public String queryForString(String query) {
    List<String> results = query(query, new ResultRowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString(1);
      }
    });
    assert results.size() == 1;
    return results.get(0);
  }

  @Override
  @TransactionalAware
  public <T> List<T> query(String query, ResultRowMapper<T> rowMapper) {
    return query(query, nullParameterSource, rowMapper);
  }

  @Override
  @TransactionalAware
  public <T> List<T> query(String query, ParameterSource parameterSource,
      ResultRowMapper<T> rowMapper) {
    Connection c = null;
    boolean inTransaction = false;

    if (ConnectionHolder.isTransaction()) {
      inTransaction = true;
      c = ConnectionHolder.getConnection();
      if (c == null) {
        c = m.createConnection();
        ConnectionHolder.setConnection(c);
      }
    } else {
      c = m.createConnection();
    }

    PreparedStatement ps = null;
    ResultSet rs = null;

    List<T> results = new ArrayList<T>();
    try {
      ps = c.prepareStatement(query);
      parameterSource.setParameters(ps);
      rs = ps.executeQuery();
      int rowNum = 0;
      while (rs.next()) {
        results.add(rowMapper.mapRow(rs, rowNum));
        rowNum++;
      }
    } catch (SQLException e) {
      throw new RuntimeSQLException(e);
    } finally {
      if (!inTransaction) {
        try {
          if (rs != null) {
            rs.close();
          }
          if (ps != null) {
            ps.close();
          }
          c.close();
        } catch (SQLException e) {
          throw new RuntimeSQLException(e);
        }
      }
    }
    return Collections.unmodifiableList(results);
  }

  @Override
  @TransactionalAware
  public <T> T queryForObject(String query, ParameterSource parameterSource,
      ResultRowMapper<T> rowMapper) {

    List<T> results = query(query, parameterSource, rowMapper);

    switch (results.size()) {
    case 0:
      return null;
    case 1:
      return results.get(0);

    default:
      assert results.size() > 1;
      StringBuilder sb = new StringBuilder();
      sb.append("Too many results").append(NEWLINE);
      sb.append("query=").append(query).append(NEWLINE);
      sb.append("results=[").append(NEWLINE);
      for (T t : results) {
        sb.append(t).append(",").append(NEWLINE);
      }
      sb.append("results=]").append(NEWLINE);
      throw new RuntimeException(sb.toString());

    }
  }

  @Override
  @TransactionalAware
  public boolean execute(String sql) {
    Connection c = null;
    boolean inTransaction = false;

    if (ConnectionHolder.isTransaction()) {
      inTransaction = true;
      c = ConnectionHolder.getConnection();
      if (c == null) {
        c = m.createConnection();
        ConnectionHolder.setConnection(c);
      }
    } else {
      c = m.createConnection();
    }

    Statement st = null;
    try {
      st = c.createStatement();
      return st.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeSQLException(e);
    } finally {
      if (!inTransaction) {
        try {
          if (st != null) {
            st.close();
          }
          c.close();
        } catch (SQLException e) {
          throw new RuntimeSQLException(e);
        }
      }
    }
  }

  @Override
  @TransactionalAware
  public <T> T update(PreparedStatementCreator creator,
      ResultRowMapper<T> rowMapper) {
    Connection c = null;
    boolean inTransaction = false;

    if (ConnectionHolder.isTransaction()) {
      inTransaction = true;
      c = ConnectionHolder.getConnection();
      if (c == null) {
        c = m.createConnection();
        ConnectionHolder.setConnection(c);
      }
    } else {
      c = m.createConnection();
    }

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = c
          .prepareStatement(creator.getSql(), Statement.RETURN_GENERATED_KEYS);
      creator.setParameters(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        T t = rowMapper.mapRow(rs, 0);
        return t;
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeSQLException(e);
    } finally {
      if (!inTransaction) {
        try {
          if (rs != null) {
            rs.close();
          }
          if (ps != null) {
            ps.close();
          }
          c.close();
        } catch (SQLException e) {
          throw new RuntimeSQLException(e);
        }
      }
    }
  }

  @Override
  @TransactionalAware
  public int update(PreparedStatementCreator creator) {

    Connection c = null;
    boolean inTransaction = false;

    if (ConnectionHolder.isTransaction()) {
      inTransaction = true;
      c = ConnectionHolder.getConnection();
      if (c == null) {
        c = m.createConnection();
        ConnectionHolder.setConnection(c);
      }
    } else {
      c = m.createConnection();
    }

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = c.prepareStatement(creator.getSql());
      creator.setParameters(ps);
      return ps.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeSQLException(e);
    } finally {
      if (!inTransaction) {
        try {
          if (rs != null) {
            rs.close();
          }
          if (ps != null) {
            ps.close();
          }
          c.close();
        } catch (SQLException e) {
          throw new RuntimeSQLException(e);
        }
      }
    }
  }

}
