package com.onyem.jtracer.reader.events.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.events.model.internal.MethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodExitInvocationEvent;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.IMethod;

@Immutable
class MethodEventRowMapper implements ResultRowMapper<IInvocationEvent> {

  private final IMetaService metaService;
  private final IEventServiceExtended eventService;

  MethodEventRowMapper(IMetaService metaService,
      IEventServiceExtended eventService) {
    this.metaService = metaService;
    this.eventService = eventService;
  }

  @Override
  public IInvocationEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
    long id = rs.getLong("ID");
    long filePosition = rs.getLong("POSITION");
    String typeString = rs.getString("EVENT_TYPE");
    long threadId = rs.getLong("THREAD_ID");
    long methodId = rs.getLong("METHOD_ID");

    InvocationEventType eventType = InvocationEventType.parseString(typeString);
    IInvocationThread thread = eventService.getOrInsertThreadById(threadId);
    IMethod method = metaService.getMethodById(methodId);

    switch (eventType) {
    case MethodEntry:
      return new MethodEntryInvocationEvent(id, filePosition, thread, method);
    case MethodExit:
      return new MethodExitInvocationEvent(id, filePosition, thread, method);

    default:
      throw new IllegalArgumentException();
    }
  }
}
