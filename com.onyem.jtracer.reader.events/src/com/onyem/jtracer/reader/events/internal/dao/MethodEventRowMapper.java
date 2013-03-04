package com.onyem.jtracer.reader.events.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.IMethodTraceInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.events.model.internal.ExceptionCatchInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.ExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodThrowExitInvocationEvent;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.IMethod;

/**
 * MethodEventRowMapper does not follow the {@link ResultRowMapper} interface of
 * returning an Object per mapRow(). Iteration over multiple rows might be
 * required to realise an {@link IInovcationEvent} like for
 * {@link IMethodTraceInvocationEvent}
 * 
 */
class MethodEventRowMapper implements ResultRowMapper<Void> {

  private final IMetaService metaService;
  private final IEventServiceExtended eventService;
  private final List<IInvocationEvent> events = new ArrayList<IInvocationEvent>();

  // State for the previous trace event
  private static long FAKE_ID = -1;
  private long prevId = FAKE_ID;
  private long prevFilePosition = FAKE_ID;
  private InvocationEventType prevEventType = null;
  private IInvocationThread prevThread = null;
  private List<IMethod> methodTrace = null;

  MethodEventRowMapper(IMetaService metaService,
      IEventServiceExtended eventService) {
    this.metaService = metaService;
    this.eventService = eventService;
  }

  @Override
  public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
    long id = rs.getLong("ID");
    long filePosition = rs.getLong("POSITION");
    String typeString = rs.getString("EVENT_TYPE");
    long threadId = rs.getLong("THREAD_ID");
    long methodId = rs.getLong("METHOD_ID");

    // If we had a prev event and now have a new event then create the prev one first
    if (prevId != FAKE_ID && prevId != id) {
      createPrevEvent();
    }

    InvocationEventType eventType = InvocationEventType.parseString(typeString);
    IInvocationThread thread = eventService.getOrInsertThreadById(threadId);
    IMethod method = metaService.getMethodById(methodId);

    switch (eventType) {
    case MethodEntry:
      events.add(new MethodEntryInvocationEvent(id, filePosition, thread,
          method));
      break;
    case MethodExit:
      events
          .add(new MethodExitInvocationEvent(id, filePosition, thread, method));
      break;
    case MethodThrowExit:
      events.add(new MethodThrowExitInvocationEvent(id, filePosition, thread,
          method));
      break;

    case ExceptionThrow:
    case ExceptionCatch:
      // New event
      if (prevId == FAKE_ID) {
        prevId = id;
        prevFilePosition = filePosition;
        prevEventType = eventType;
        prevThread = thread;
        methodTrace = new ArrayList<IMethod>();
      }
      // Just add to the trace
      methodTrace.add(method);
      break;

    default:
      throw new IllegalArgumentException();
    }
    return null;
  }

  private void createPrevEvent() {
    // Create the event
    if (prevEventType == InvocationEventType.ExceptionThrow) {
      events.add(new ExceptionThrowInvocationEvent(prevId, prevFilePosition,
          prevThread, methodTrace));
    } else if (prevEventType == InvocationEventType.ExceptionCatch) {
      events.add(new ExceptionCatchInvocationEvent(prevId, prevFilePosition,
          prevThread, methodTrace));
    } else {
      throw new IllegalStateException();
    }

    // Reset the data
    prevId = FAKE_ID;
    prevFilePosition = FAKE_ID;
    prevEventType = null;
    prevThread = null;
    methodTrace = null;
  }

  // Get the results
  public List<IInvocationEvent> getEvents() {
    if (prevId != FAKE_ID) {
      createPrevEvent();
    }
    return Collections.unmodifiableList(events);
  }
}
