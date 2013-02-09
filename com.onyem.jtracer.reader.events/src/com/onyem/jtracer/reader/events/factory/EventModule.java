package com.onyem.jtracer.reader.events.factory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.onyem.jtracer.reader.events.factory.internal.EventFileDAOFactory;
import com.onyem.jtracer.reader.events.factory.internal.EventsDAOFactory;
import com.onyem.jtracer.reader.events.factory.internal.InvocationEventCreatorFactory;
import com.onyem.jtracer.reader.events.factory.internal.ThreadDAOFactory;
import com.onyem.jtracer.reader.events.internal.EventServiceFactory;
import com.onyem.jtracer.reader.events.internal.InvocationEventCreator;
import com.onyem.jtracer.reader.events.internal.dao.EventFileDAO;
import com.onyem.jtracer.reader.events.internal.dao.EventsDAO;
import com.onyem.jtracer.reader.events.internal.dao.ThreadDAO;

public class EventModule extends AbstractModule {

  @Override
  protected void configure() {

    install(new FactoryModuleBuilder().implement(EventFileDAO.class,
        EventFileDAO.class).build(EventFileDAOFactory.class));

    install(new FactoryModuleBuilder().implement(EventsDAO.class,
        EventsDAO.class).build(EventsDAOFactory.class));

    install(new FactoryModuleBuilder().implement(ThreadDAO.class,
        ThreadDAO.class).build(ThreadDAOFactory.class));

    install(new FactoryModuleBuilder().implement(InvocationEventCreator.class,
        InvocationEventCreator.class)
        .build(InvocationEventCreatorFactory.class));

    bind(IEventServiceFactory.class).to(EventServiceFactory.class);
  }
}
