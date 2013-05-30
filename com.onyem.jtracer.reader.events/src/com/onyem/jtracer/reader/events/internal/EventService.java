package com.onyem.jtracer.reader.events.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.events.EventLoadOptions;
import com.onyem.jtracer.reader.events.internal.converter.IEventConverter;
import com.onyem.jtracer.reader.events.internal.converter.LoopEventLoader;
import com.onyem.jtracer.reader.events.internal.converter.NullConverter;
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
    return getNextEvent(new EventLoadOptions(), startEvent);
  }

  @Override
  public List<IInvocationEvent> getNextEvent(EventLoadOptions loadOptions,
      IInvocationEvent startEvent) {
    IEventConverter eventConverter = loadOptions.isEnableLoopEvents() ? new LoopEventLoader(
        eventsLoadCount) : new NullConverter(eventsLoadCount);

    EventFile eventFile = eventFileDAO.getOrInsertEventFileByName(name);
    return getNextEvents(eventFile, startEvent, eventConverter);
  }

  private IInvocationEvent getFirstEvent(EventFile eventFile) {
    if (eventFile.getFirstEvent() == null) {
      // eventFile has never been parsed and so the firstEvent is not
      // in the database
      List<IInvocationEvent> events = getEventsFromParser(
          IEventParser.START_POSITION, eventFile, 1);
      IInvocationEvent firstEvent = events.get(0);
      eventFileDAO.insertFirstEvent(eventFile, firstEvent);
      return firstEvent;
    } else {
      return eventFile.getFirstEvent();
    }
  }

  private List<IInvocationEvent> getNextEvents(EventFile eventFile,
      IInvocationEvent startEvent, IEventConverter eventConverter) {
    boolean dbContainsEvents = true;
    boolean complete = false;

    while (!complete && eventConverter.loadMoreEvents()) {

      if (startEvent == null) {
        startEvent = getFirstEvent(eventFile);
        eventConverter.convertEvents(Collections.singletonList(startEvent),
            complete);

      } else if (dbContainsEvents) {
        // Can we get events from the DB?
        List<IInvocationEvent> dbEvents = eventsDAO.getEventsAfterId(
            startEvent, eventConverter.getFetchCount());
        if (dbEvents.isEmpty()) {
          dbContainsEvents = false;
        } else {
          startEvent = dbEvents.get(dbEvents.size() - 1);
          eventConverter.convertEvents(dbEvents, complete);
        }

      } else if (eventFile.getLastEvent() != null
          && startEvent.equals(eventFile.getLastEvent())) {
        complete = true;
        List<IInvocationEvent> none = Collections.emptyList();
        eventConverter.convertEvents(none, complete);

      } else {
        // Continue from parser if we are not at the end of the file?
        // Since we are parsing more then the end of the file has not been reached
        assert eventFile.getLastEvent() == null;

        long continueStartPosition = startEvent.getFilePosition();
        List<IInvocationEvent> eventsFromParser = getEventsFromParser(
            continueStartPosition, eventFile, eventConverter.getFetchCount());

        // Since even the parser does not have more events we have reached 
        // the end of the file
        if (eventsFromParser.size() < eventConverter.getFetchCount()) {
          IInvocationEvent finalEvent = null;

          if (!eventsFromParser.isEmpty()) {
            finalEvent = eventsFromParser.get(eventsFromParser.size() - 1);
          } else {
            finalEvent = startEvent;
          }

          complete = true;
          if (finalEvent != null) {
            eventFileDAO.insertLastEvent(eventFile, finalEvent);
          }
        }
        // Add all the events from parser
        eventConverter.convertEvents(eventsFromParser, complete);
      }
    }
    return Collections.unmodifiableList(eventConverter.getEvents());
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
