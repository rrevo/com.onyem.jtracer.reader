package com.onyem.jtracer.reader.events.factory.internal;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.events.internal.IEventServiceExtended;
import com.onyem.jtracer.reader.events.internal.dao.EventFileDAO;

public interface EventFileDAOFactory {

  EventFileDAO create(@Assisted IConnectionManager connectionManager,
      @Assisted IEventServiceExtended eventService);
}
