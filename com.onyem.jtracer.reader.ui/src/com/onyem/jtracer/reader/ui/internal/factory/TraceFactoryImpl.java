package com.onyem.jtracer.reader.ui.internal.factory;

import com.google.inject.Inject;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;
import com.onyem.jtracer.reader.db.factory.IJdbcHelperFactory;
import com.onyem.jtracer.reader.events.factory.IEventServiceFactory;
import com.onyem.jtracer.reader.meta.factory.IMetaServiceFactory;
import com.onyem.jtracer.reader.parser.factory.EventParserFactory;
import com.onyem.jtracer.reader.parser.factory.MetaParserFactory;
import com.onyem.jtracer.reader.parser.factory.PropertiesParserFactory;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;
import com.onyem.jtracer.reader.ui.editors.trace.ui.TraceResultClient;
import com.onyem.jtracer.reader.ui.factory.TraceFactory;

public class TraceFactoryImpl implements TraceFactory {

  private final PropertiesParserFactory propertiesParserFactory;
  private final IConnectionManagerFactory connectionManagerFactory;
  private final IJdbcHelperFactory jdbcHelperFactory;
  private final MetaParserFactory metaParserFactory;
  private final IMetaServiceFactory metaServiceFactory;
  private final EventParserFactory eventParserFactory;
  private final IEventServiceFactory eventServiceFactory;

  @Inject
  TraceFactoryImpl(PropertiesParserFactory propertiesParserFactory,
      IConnectionManagerFactory connectionManagerFactory,
      IJdbcHelperFactory jdbcHelperFactory,
      MetaParserFactory metaParserFactory,
      IMetaServiceFactory metaServiceFactory,
      EventParserFactory eventParserFactory,
      IEventServiceFactory eventServiceFactory) {
    this.propertiesParserFactory = propertiesParserFactory;
    this.connectionManagerFactory = connectionManagerFactory;
    this.jdbcHelperFactory = jdbcHelperFactory;
    this.metaParserFactory = metaParserFactory;
    this.metaServiceFactory = metaServiceFactory;
    this.eventParserFactory = eventParserFactory;
    this.eventServiceFactory = eventServiceFactory;
  }

  public void create(final String applicationPath,
      final TraceResultClient traceResultClient) {
    final IQueueService queueService = Activator.getQueue();
    queueService.queueNow(new Runnable() {

      @Override
      public void run() {
        try {
          Trace trace = new Trace(propertiesParserFactory,
              connectionManagerFactory, jdbcHelperFactory, metaParserFactory,
              metaServiceFactory, eventParserFactory, eventServiceFactory,
              queueService, applicationPath);
          traceResultClient.setTraceResult(trace, null);
        } catch (Exception e) {
          traceResultClient.setTraceResult(null, e);
        }
      }

    });
  }
}
