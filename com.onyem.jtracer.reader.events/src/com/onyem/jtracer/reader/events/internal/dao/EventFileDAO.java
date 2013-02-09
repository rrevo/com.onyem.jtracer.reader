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
import com.onyem.jtracer.reader.db.util.LongResultRowMapper;
import com.onyem.jtracer.reader.db.util.StringParameterSource;
import com.onyem.jtracer.reader.events.internal.EventFile;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;

@DAO
@Immutable
public class EventFileDAO {

  private final IJdbcHelper helper;
  private final IEventServiceExtended eventService;

  @Inject
  public EventFileDAO(IJdbcHelperFactory helperFactory,
      @Assisted IConnectionManager connectionManager,
      @Assisted IEventServiceExtended eventService) {
    helper = helperFactory.create(connectionManager);
    this.eventService = eventService;
  }

  @Transactional
  public EventFile getOrInsertEventFileByName(final String name) {
    EventFile eventFile = getEventFileByName(name);
    if (eventFile == null) {
      eventFile = insertEventFile(name);
    }
    return eventFile;
  }

  @Transactional
  public EventFile getEventFileByName(final String name) {
    ParameterSource parameterSource = new StringParameterSource(name);

    EventFileResultRowMapper rowMapper = new EventFileResultRowMapper(
        eventService);
    EventFile eventFile = helper.queryForObject(
        "SELECT EF.ID, EF.NAME, EFE.FIRST_EVENT_ID, EFE.LAST_EVENT_ID "
            + "FROM EVENT_FILES EF JOIN EVENT_FILE_EVENTS EFE "
            + "WHERE EF.ID = EFE.ID AND EF.NAME = ?", parameterSource,
        rowMapper);

    return eventFile;
  }

  private EventFile insertEventFile(final String eventFileName) {
    final long id = helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        statement.setString(1, eventFileName);
      }

      @Override
      public String getSql() {
        return "INSERT INTO EVENT_FILES (NAME) VALUES (?)";
      }

    }, new LongResultRowMapper());
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        statement.setLong(1, id);
      }

      @Override
      public String getSql() {
        return "INSERT INTO EVENT_FILE_EVENTS (ID) VALUES (?)";
      }

    });
    return getEventFileByName(eventFileName);
  }

  @Transactional
  public EventFile insertFirstEvent(EventFile eventFile,
      IInvocationEvent firstEvent) {
    return insertEventFileEvent("FIRST_EVENT_ID", eventFile, firstEvent);
  }

  @Transactional
  public EventFile insertLastEvent(EventFile eventFile,
      IInvocationEvent lastEvent) {
    return insertEventFileEvent("LAST_EVENT_ID", eventFile, lastEvent);
  }

  private EventFile insertEventFileEvent(final String columnName,
      final EventFile eventFile, final IInvocationEvent event) {
    Long idLong = helper.queryForObject(
        "SELECT ID FROM EVENT_FILE_EVENTS WHERE ID = ? AND " + columnName
            + " IS NULL", new ParameterSource() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setLong(1, eventFile.getId());
          }
        }, new LongResultRowMapper());
    if (idLong == null || idLong.longValue() != eventFile.getId()) {
      throw new IllegalStateException(columnName + " is already set for "
          + eventFile);
    }
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        statement.setLong(1, event.getId());
        statement.setLong(2, eventFile.getId());
      }

      @Override
      public String getSql() {
        return "UPDATE EVENT_FILE_EVENTS SET " + columnName + " = ? "
            + "WHERE ID = ?";
      }

    });
    return getEventFileByName(eventFile.getName());
  }
}
