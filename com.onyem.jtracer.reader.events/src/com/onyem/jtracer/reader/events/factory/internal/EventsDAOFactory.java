package com.onyem.jtracer.reader.events.factory.internal;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.internal.dao.EventsDAO;
import com.onyem.jtracer.reader.meta.IMetaService;

public interface EventsDAOFactory {

  EventsDAO create(@Assisted IConnectionManager connectionManager,
      @Assisted IMetaService metaService,
      @Assisted IEventServiceExtended eventService);
}
