package com.onyem.jtracer.reader.events.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.events.internal.EventFile;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;

@Immutable
class EventFileResultRowMapper implements ResultRowMapper<EventFile> {

  private final IEventServiceExtended eventService;

  public EventFileResultRowMapper(IEventServiceExtended eventService) {
    this.eventService = eventService;
  }

  @Override
  public EventFile mapRow(ResultSet rs, int rowNum) throws SQLException {
    long id = rs.getLong("ID");
    String name = rs.getString("NAME");
    Long firstEventId = rs.getLong("FIRST_EVENT_ID");
    IInvocationEvent firstEvent = null;
    if (!rs.wasNull()) {
      firstEvent = eventService.getEventById(firstEventId);
    }
    Long lastEventId = rs.getLong("LAST_EVENT_ID");
    IInvocationEvent lastEvent = null;
    if (!rs.wasNull()) {
      lastEvent = eventService.getEventById(lastEventId);
    }
    return new EventFile(id, name, firstEvent, lastEvent);
  }
}
