package com.onyem.jtracer.reader.events.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.events.internal.dao.EventFileDAO;
import com.onyem.jtracer.reader.events.internal.dao.EventsDAO;
import com.onyem.jtracer.reader.events.internal.dao.ThreadDAO;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IInvocationThread;
import com.onyem.jtracer.reader.parser.IEventParser;
import com.onyem.jtracer.reader.parser.ILine;
import com.onyem.jtracer.reader.queue.IQueueService;

@Service
@ThreadSafe
public class EventService implements IEventServiceExtended {

  private final int eventsLoadCount;

  private final String name;
  private final IEventParser eventParser;
  private final InvocationEventCreator eventCreator;
  private EventFileDAO eventFileDAO;
  private EventsDAO eventsDAO;
  private ThreadDAO threadDAO;

  public EventService(IEventParser eventParser,
      InvocationEventCreator eventCreator, int eventsLoadCount) {
    name = eventParser.getName();
    this.eventParser = eventParser;
    this.eventCreator = eventCreator;
    this.eventsLoadCount = eventsLoadCount;

    assert eventsLoadCount > 0;
  }

  synchronized void setEventFileDAO(EventFileDAO eventFileDAO) {
    this.eventFileDAO = eventFileDAO;
  }

  synchronized void setEventsDAO(EventsDAO eventsDAO) {
    this.eventsDAO = eventsDAO;
  }

  synchronized void setThreadDAO(ThreadDAO threadDAO) {
    this.threadDAO = threadDAO;
  }

  @Override
  public synchronized void close() throws IOException {
    eventParser.close();
  }

  @Override
  public EventFile getEventFileByName(String name) {
    EventFile eventFile = eventFileDAO.getEventFileByName(name);
    return eventFile;
  }

  @Override
  public synchronized List<IInvocationEvent> getNextEvent(
      IInvocationEvent startEvent) {
    EventFile eventFile = eventFileDAO.getOrInsertEventFileByName(name);
    if (startEvent == null && eventFile.getFirstEvent() == null) {
      List<IInvocationEvent> events = getEventsFromParser(
          IEventParser.START_POSITION, eventFile, 1);
      IInvocationEvent firstEvent = events.get(0);
      eventFileDAO.insertFirstEvent(eventFile, firstEvent);
      events.addAll(getNextEvent(eventFile, firstEvent, eventsLoadCount - 1));
      return Collections.unmodifiableList(events);
    } else {
      return getNextEvent(eventFile, startEvent, eventsLoadCount);
    }
  }

  private List<IInvocationEvent> getNextEvent(EventFile eventFile,
      IInvocationEvent startEvent, int count) {
    List<IInvocationEvent> events = new ArrayList<IInvocationEvent>();

    // Can we get all the events from the DB?
    events.addAll(eventsDAO.getEventsAfterId(startEvent, count));

    if (events.size() < count) {
      IInvocationEvent lastEvent = events.isEmpty() ? startEvent : events
          .get(events.size() - 1);

      // Continue from parser if we are not at the end of the file?
      if (!lastEvent.equals(eventFile.getLastEvent())) {

        // Since we are parsing more then the end of the file has not been reached
        assert eventFile.getLastEvent() == null;

        long continueStartPosition = lastEvent.getFilePosition();
        List<IInvocationEvent> eventsFromParser = getEventsFromParser(
            continueStartPosition, eventFile, count - events.size());

        // Since even the parser does not have more events we have reached 
        // the end of the file
        if ((events.size() + eventsFromParser.size()) < count) {
          IInvocationEvent finalEvent = null;

          if (!eventsFromParser.isEmpty()) {
            finalEvent = eventsFromParser.get(eventsFromParser.size() - 1);
          } else if (!events.isEmpty()) {
            finalEvent = events.get(events.size() - 1);
          } else if (startEvent != null) {
            finalEvent = startEvent;
          }

          if (finalEvent != null) {
            eventFileDAO.insertLastEvent(eventFile, finalEvent);
          }
        }
        // Add all the events from parser
        events.addAll(eventsFromParser);
      }
    }
    return Collections.unmodifiableList(events);
  }

  private List<IInvocationEvent> getEventsFromParser(long startPosition,
      EventFile eventFile, int count) {
    // Get lines from the EventFile
    List<ILine> lines = eventParser.getLines(startPosition, count);
    List<IInvocationEvent> parserEvents = eventCreator.create(this, lines);

    // Insert parser events into the Db
    List<IInvocationEvent> dbEvents = new ArrayList<IInvocationEvent>();
    for (IInvocationEvent event : parserEvents) {
      IInvocationEvent newEvent = eventsDAO.insertEvent(eventFile, event);
      dbEvents.add(newEvent);
    }
    assert parserEvents.size() == dbEvents.size();

    return dbEvents;
  }

  @Override
  public synchronized IInvocationEvent getEventById(long id) {
    return eventsDAO.getEventById(id);
  }

  @Override
  public synchronized IInvocationThread getOrInsertThreadById(long id) {
    return threadDAO.getOrInsertThreadById(id);
  }

  @Override
  public void loadEvents(final IQueueService queueService) {
    queueService.queueLater(new Runnable() {

      @Override
      public void run() {
        EventFile eventFile = eventFileDAO.getOrInsertEventFileByName(name);
        IInvocationEvent lastLoadedEvent = eventsDAO
            .getLastLoadedEvent(eventFile);
        loadEvents(queueService, lastLoadedEvent);
      }
    });
  }

  private void loadEvents(final IQueueService queueService,
      final IInvocationEvent lastEvent) {
    queueService.queueLater(new Runnable() {

      @Override
      public void run() {
        List<IInvocationEvent> nextEvents = EventService.this
            .getNextEvent(lastEvent);
        if (!nextEvents.isEmpty()) {
          IInvocationEvent newLastEvent = nextEvents.get(nextEvents.size() - 1);
          loadEvents(queueService, newLastEvent);
        }
      }
    });
  }
}
