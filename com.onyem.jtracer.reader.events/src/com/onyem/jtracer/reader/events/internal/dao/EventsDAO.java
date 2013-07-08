package com.onyem.jtracer.reader.events.internal.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.DAO;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.PreparedStatementCreator;
import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.db.Transactional;
import com.onyem.jtracer.reader.db.factory.IJdbcHelperFactory;
import com.onyem.jtracer.reader.db.util.LongParameterSource;
import com.onyem.jtracer.reader.events.internal.EventFile;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodTraceInvocationEvent;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.IMethod;

@DAO
@Immutable
public class EventsDAO {

  private final IJdbcHelper helper;
  private final IMetaService metaService;
  private final IEventServiceExtended eventService;

  @Inject
  public EventsDAO(IJdbcHelperFactory helperFactory,
      @Assisted IConnectionManager connectionManager,
      @Assisted IMetaService metaService,
      @Assisted IEventServiceExtended eventService) {
    helper = helperFactory.create(connectionManager);
    this.metaService = metaService;
    this.eventService = eventService;
  }

  @Transactional
  public IInvocationEvent getEventById(final long id) {
    ParameterSource parameterSource = new LongParameterSource(id);

    MethodEventRowMapper rowMapper = new MethodEventRowMapper(metaService,
        eventService);
    helper.query(
        "SELECT E.ID, E.POSITION, E.EVENT_TYPE, E.THREAD_ID, EM.METHOD_ID "
            + "FROM EVENTS E JOIN EVENT_METHODS EM "
            + "WHERE E.ID = EM.EVENT_ID AND E.ID = ? ", parameterSource,
        rowMapper);
    return queryForObject(rowMapper);
  }

  private IInvocationEvent queryForObject(MethodEventRowMapper rowMapper) {
    List<IInvocationEvent> events = rowMapper.getEvents();
    int size = events.size();
    switch (size) {
    case 0:
      return null;
    case 1:
      return events.get(0);
    default:
      throw new RuntimeException();
    }
  }

  @Transactional
  public List<IInvocationEvent> getEventsAfterId(final IInvocationEvent event,
      final int count) {
    if (event == null) {
      MethodEventRowMapper rowMapper = new MethodEventRowMapper(metaService,
          eventService);
      helper.query(
          "SELECT E.ID, E.POSITION, E.EVENT_TYPE, E.THREAD_ID, EM.METHOD_ID "
              + "FROM EVENTS E JOIN EVENT_METHODS EM "
              + "WHERE E.ID = EM.EVENT_ID " + "ORDER BY E.ID " + "LIMIT "
              + count, rowMapper);
      return rowMapper.getEvents();
    } else {
      if (!event.getType().isExistsInTrace()) {
        throw new IllegalArgumentException("Invalid event: " + event);
      }

      ParameterSource parameterSource = new LongParameterSource(event.getId());
      MethodEventRowMapper rowMapper = new MethodEventRowMapper(metaService,
          eventService);
      helper.query(
          "SELECT E.ID, E.POSITION, E.EVENT_TYPE, E.THREAD_ID, EM.METHOD_ID "
              + "FROM EVENTS E JOIN EVENT_METHODS EM "
              + "WHERE E.ID = EM.EVENT_ID AND E.ID > ? " + "ORDER BY E.ID "
              + "LIMIT " + count, parameterSource, rowMapper);
      return rowMapper.getEvents();
    }
  }

  @Transactional
  public IInvocationEvent insertEvent(final EventFile eventFile,
      final IInvocationEvent event) {
    switch (event.getType()) {
    case MethodEntry:
    case MethodExit:
    case MethodThrowExit:
      return insertEvent(eventFile, (IMethodInvocationEvent) event);

    case ExceptionThrow:
    case ExceptionCatch:
      return insertEvent(eventFile, (IMethodTraceInvocationEvent) event);

    default:
      throw new IllegalArgumentException();
    }

  }

  private IInvocationEvent insertEvent(final EventFile eventFile,
      final IMethodInvocationEvent event) {
    final long id = helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        int index = 1;
        statement.setLong(index++, eventFile.getId());
        statement.setLong(index++, event.getFilePosition());
        statement.setString(index++, event.getType().getValue());
        statement.setLong(index++, event.getThread().getId());
      }

      @Override
      public String getSql() {
        return "INSERT INTO EVENTS (EVENT_FILE_ID, POSITION, EVENT_TYPE, THREAD_ID) "
            + "VALUES (?, ?, ?, ?)";
      }

    }, new ResultRowMapper<Long>() {

      @Override
      public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
      }
    });

    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        statement.setLong(1, id);
        statement.setLong(2, event.getMethod().getId().getId());
      }

      @Override
      public String getSql() {
        return "INSERT INTO EVENT_METHODS (EVENT_ID, METHOD_ID) VALUES (?, ?)";
      }
    });
    return getEventById(id);
  }

  private IInvocationEvent insertEvent(final EventFile eventFile,
      final IMethodTraceInvocationEvent event) {
    final long id = helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        int index = 1;
        statement.setLong(index++, eventFile.getId());
        statement.setLong(index++, event.getFilePosition());
        statement.setString(index++, event.getType().getValue());
        statement.setLong(index++, event.getThread().getId());
      }

      @Override
      public String getSql() {
        return "INSERT INTO EVENTS (EVENT_FILE_ID, POSITION, EVENT_TYPE, THREAD_ID) "
            + "VALUES (?, ?, ?, ?)";
      }

    }, new ResultRowMapper<Long>() {

      @Override
      public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
      }
    });

    for (final IMethod methodTrace : event.getMethodTrace()) {
      helper.update(new PreparedStatementCreator() {

        @Override
        public void setParameters(PreparedStatement statement)
            throws SQLException {
          statement.setLong(1, id);
          statement.setLong(2, methodTrace.getId().getId());
        }

        @Override
        public String getSql() {
          return "INSERT INTO EVENT_METHODS (EVENT_ID, METHOD_ID) VALUES (?, ?)";
        }
      });
    }
    return getEventById(id);
  }

  @Transactional
  public IInvocationEvent getLastLoadedEvent(EventFile eventFile) {
    ParameterSource parameterSource = new LongParameterSource(eventFile.getId());
    MethodEventRowMapper rowMapper = new MethodEventRowMapper(metaService,
        eventService);
    helper.query(
        "SELECT E.ID, E.POSITION, E.EVENT_TYPE, E.THREAD_ID, EM.METHOD_ID "
            + "FROM EVENTS E JOIN EVENT_METHODS EM "
            + "WHERE E.ID = EM.EVENT_ID AND E.ID = "
            + " (SELECT MAX(E2.ID) FROM EVENTS E2 WHERE EVENT_FILE_ID = ? )",
        parameterSource, rowMapper);
    return queryForObject(rowMapper);
  }
}
