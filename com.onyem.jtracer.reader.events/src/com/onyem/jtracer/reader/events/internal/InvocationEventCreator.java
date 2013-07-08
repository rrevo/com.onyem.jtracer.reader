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
import com.onyem.jtracer.reader.events.model.internal.ExceptionCatchInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.ExceptionThrowInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodEntryInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodExitInvocationEvent;
import com.onyem.jtracer.reader.events.model.internal.MethodThrowExitInvocationEvent;
import com.onyem.jtracer.reader.meta.IClass;
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
    if (type == InvocationEventType.MethodEntry
        || type == InvocationEventType.MethodExit
        || type == InvocationEventType.MethodThrowExit) {

      Long methodId = Long.parseLong(dataParts[2]);
      IMethod method = metaService.getMethodByMetaId(methodId);
      switch (type) {
      case MethodEntry:
        return new MethodEntryInvocationEvent(Constants.NULL_ID, position,
            thread, method);
      case MethodExit:
        return new MethodExitInvocationEvent(Constants.NULL_ID, position,
            thread, method);
      case MethodThrowExit:
        return new MethodThrowExitInvocationEvent(Constants.NULL_ID, position,
            thread, method);
      default:
        throw new IllegalArgumentException();
      }
    }
    if (type == InvocationEventType.ExceptionThrow
        || type == InvocationEventType.ExceptionCatch) {

      List<IMethod> methodTrace = new ArrayList<IMethod>();
      int stackSize = Integer.parseInt(dataParts[2]);
      for (int stack = 0; stack < stackSize; stack++) {
        int baseIndex = 3 + (stack * 4);
        IMethod method = parseMethod(dataParts, baseIndex + 1, baseIndex + 2,
            baseIndex + 3);
        methodTrace.add(method);
      }
      switch (type) {
      case ExceptionThrow:
        return new ExceptionThrowInvocationEvent(Constants.NULL_ID, position,
            thread, methodTrace);
      case ExceptionCatch:
        return new ExceptionCatchInvocationEvent(Constants.NULL_ID, position,
            thread, methodTrace);
      default:
        throw new IllegalArgumentException();
      }
    }
    throw new IllegalArgumentException();
  }

  private IMethod parseMethod(String[] parts, int classNameIndex,
      int methodNameIndex, int methodSignatureIndex) {
    // For exceptions we do not store all the method or class metadata
    String className = parts[classNameIndex];
    IClass clazz = metaService.getPlainClassByName(className.replace("/", "."));

    String methodName = parts[methodNameIndex];
    String methodSignature = parts[methodSignatureIndex];

    IMethod method = metaService.getMethodByNameDescription(methodName,
        methodSignature, clazz.getId());
    return method;
  }
}
