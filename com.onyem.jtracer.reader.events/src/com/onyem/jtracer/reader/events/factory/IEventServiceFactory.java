package com.onyem.jtracer.reader.events.factory;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.events.IEventService;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.parser.IEventParser;

public interface IEventServiceFactory {

  IEventService create(IConnectionManager connectionManager,
      IEventParser eventParser, IMetaService metaService);

  IEventService create(IConnectionManager connectionManager,
      IEventParser eventParser, IMetaService metaService,
      @Assisted int eventLoadCount);

}
