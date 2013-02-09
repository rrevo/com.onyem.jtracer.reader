package com.onyem.jtracer.reader.events.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.util.Constants;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.events.model.internal.MethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodExitInvocationEvent;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.parser.ILine;

@Immutable
public class InvocationEventCreator {

  private final IMetaService metaService;

  @Inject
  InvocationEventCreator(@Assisted IMetaService metaService) {
    this.metaService = metaService;
  }

  List<IInvocationEvent> create(IEventServiceExtended eventService,
      List<ILine> lines) {
    List<IInvocationEvent> events = new ArrayList<IInvocationEvent>();
    for (ILine line : lines) {
      String data = line.getData();
      String[] dataParts = getParts(data);
      long position = line.getPosition();
      IInvocationThread thread = getThreadFromLine(dataParts, eventService);
      InvocationEventType type = getTypeFromLine(dataParts);
      IInvocationEvent event = create(type, position, thread, dataParts);
      events.add(event);
    }
    return Collections.unmodifiableList(events);
  }

  private String[] getParts(String data) {
    // <+|1|2>
    // remove the < >
    data = data.substring(1, data.length() - 1);
    return data.split("\\|");
  }

  private InvocationEventType getTypeFromLine(String[] dataParts) {
    String type = dataParts[0];
    return InvocationEventType.parseString(type);
  }

  private IInvocationThread getThreadFromLine(String[] dataParts,
      IEventServiceExtended eventService) {
    long threadId = Long.parseLong(dataParts[1]);
    IInvocationThread thread = eventService.getOrInsertThreadById(threadId);
    return thread;
  }

  private IInvocationEvent create(InvocationEventType type, long position,
      IInvocationThread thread, String[] dataParts) {
    Long methodId = Long.parseLong(dataParts[2]);
    IMethod method = metaService.getMethodByMetaId(methodId);
    switch (type) {
    case MethodEntry:
      return new MethodEntryInvocationEvent(Constants.NULL_ID, position,
          thread, method);
    case MethodExit:
      return new MethodExitInvocationEvent(Constants.NULL_ID, position, thread,
          method);

    default:
      throw new IllegalArgumentException();
    }
  }
}
