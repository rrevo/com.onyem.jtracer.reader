package com.onyem.jtracer.reader.events.internal.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.DAO;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.PreparedStatementCreator;
import com.onyem.jtracer.reader.db.Transactional;
import com.onyem.jtracer.reader.db.factory.IJdbcHelperFactory;
import com.onyem.jtracer.reader.db.util.LongParameterSource;
import com.onyem.jtracer.reader.events.internal.InvocationThread;
import com.onyem.jtracer.reader.events.model.IInvocationThread;

@DAO
@Immutable
public class ThreadDAO {

  private final IJdbcHelper helper;

  @Inject
  public ThreadDAO(IJdbcHelperFactory helperFactory,
      @Assisted IConnectionManager connectionManager) {
    helper = helperFactory.create(connectionManager);
  }

  @Transactional
  public IInvocationThread getThreadById(final long id) {
    ParameterSource parameterSource = new LongParameterSource(id);

    ThreadResultRowMapper resultRowMapper = new ThreadResultRowMapper();
    IInvocationThread thread = helper.queryForObject(
        "SELECT * FROM THREADS T WHERE T.ID = ?", parameterSource,
        resultRowMapper);

    return thread;
  }

  @Transactional
  public IInvocationThread getOrInsertThreadById(long threadId) {
    IInvocationThread thread = getThreadById(threadId);
    if (thread == null) {
      thread = insertThread(new InvocationThread(threadId));
    }
    return thread;
  }

  private IInvocationThread insertThread(final IInvocationThread thread) {
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        statement.setLong(1, thread.getId());
      }

      @Override
      public String getSql() {
        return "INSERT INTO THREADS (ID) VALUES (?)";
      }

    });
    return getThreadById(thread.getId());
  }
}
