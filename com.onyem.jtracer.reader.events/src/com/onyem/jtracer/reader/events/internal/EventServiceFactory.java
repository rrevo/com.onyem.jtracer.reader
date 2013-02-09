package com.onyem.jtracer.reader.events.internal;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.events.IEventService;
import com.onyem.jtracer.reader.events.factory.IEventServiceFactory;
import com.onyem.jtracer.reader.events.factory.internal.EventFileDAOFactory;
import com.onyem.jtracer.reader.events.factory.internal.EventsDAOFactory;
import com.onyem.jtracer.reader.events.factory.internal.InvocationEventCreatorFactory;
import com.onyem.jtracer.reader.events.factory.internal.ThreadDAOFactory;
import com.onyem.jtracer.reader.events.internal.dao.EventFileDAO;
import com.onyem.jtracer.reader.events.internal.dao.EventsDAO;
import com.onyem.jtracer.reader.events.internal.dao.ThreadDAO;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.parser.IEventParser;

public class EventServiceFactory implements IEventServiceFactory {

  private final EventFileDAOFactory eventFileDaoFactory;
  private final EventsDAOFactory eventsDaoFactory;
  private final ThreadDAOFactory threadDaoFactory;
  private final InvocationEventCreatorFactory invocationEventCreatorFactory;

  @Inject
  EventServiceFactory(EventFileDAOFactory eventFileDaoFactory,
      EventsDAOFactory eventsDaoFactory, ThreadDAOFactory threadDaoFactory,
      InvocationEventCreatorFactory invocationEventCreatorFactory) {
    this.eventFileDaoFactory = eventFileDaoFactory;
    this.eventsDaoFactory = eventsDaoFactory;
    this.threadDaoFactory = threadDaoFactory;
    this.invocationEventCreatorFactory = invocationEventCreatorFactory;
  }

  @Override
  public IEventService create(IConnectionManager connectionManager,
      IEventParser eventParser, IMetaService metaService) {
    return create(connectionManager, eventParser, metaService,
        IEventServiceExtended.DEFAULT_EVENTS_COUNT);
  }

  @Override
  public IEventService create(IConnectionManager connectionManager,
      IEventParser eventParser, IMetaService metaService,
      @Assisted int eventLoadCount) {
    InvocationEventCreator invocationEventCreator = invocationEventCreatorFactory
        .create(metaService);
    EventService eventService = new EventService(eventParser,
        invocationEventCreator, eventLoadCount);

    EventFileDAO eventFileDAO = eventFileDaoFactory.create(connectionManager,
        eventService);
    eventService.setEventFileDAO(eventFileDAO);

    EventsDAO eventsDAO = eventsDaoFactory.create(connectionManager,
        metaService, eventService);
    eventService.setEventsDAO(eventsDAO);

    ThreadDAO threadDAO = threadDaoFactory.create(connectionManager);
    eventService.setThreadDAO(threadDAO);

    return eventService;
  }

}
