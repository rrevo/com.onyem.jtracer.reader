package com.onyem.jtracer.reader.events.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.factory.DbModule;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;
import com.onyem.jtracer.reader.events.EventLoadOptions;
import com.onyem.jtracer.reader.events.factory.EventModule;
import com.onyem.jtracer.reader.events.factory.IEventServiceFactory;
import com.onyem.jtracer.reader.events.model.IInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodInvocationEvent;
import com.onyem.jtracer.reader.events.model.IMethodTraceInvocationEvent;
import com.onyem.jtracer.reader.events.model.InvocationEventType;
import com.onyem.jtracer.reader.events.model.internal.InvocationEventComparator;
import com.onyem.jtracer.reader.events.model.internal.InvocationLoopEvent;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.factory.IMetaServiceFactory;
import com.onyem.jtracer.reader.meta.factory.MetaModule;
import com.onyem.jtracer.reader.parser.IEventParser;
import com.onyem.jtracer.reader.parser.IMetaParser;
import com.onyem.jtracer.reader.parser.factory.EventParserFactory;
import com.onyem.jtracer.reader.parser.factory.MetaParserFactory;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.utils.FileUtils;

public abstract class AbstractEventTest {

  protected IMetaService metaService;
  protected IEventServiceExtended eventService;

  protected String eventName;

  private static final InvocationEventComparator EVENT_COMPARATOR = new InvocationEventComparator();

  @Before
  public void setup() throws Exception {
    Injector injector = Guice.createInjector(new DbModule(), new MetaModule(),
        new ParserModule(), new EventModule());

    MetaParserFactory metaParserFactory = injector
        .getInstance(MetaParserFactory.class);

    String metaPath = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        getMetaPath());
    File file = new File(metaPath);
    RandomAccessFile metaFile = new RandomAccessFile(metaPath, "r");
    IMetaParser metaParser = metaParserFactory.create(file.getName(), metaFile);

    String dbPath = FileUtils.getTempPath("onyemdb");
    IConnectionManager manager = injector.getInstance(
        IConnectionManagerFactory.class).createWithMigration(dbPath);

    IMetaServiceFactory factory = injector
        .getInstance(IMetaServiceFactory.class);
    metaService = factory.create(manager, metaParser);

    EventParserFactory eventParserFactory = injector
        .getInstance(EventParserFactory.class);

    String eventPath = FileUtils.getPluginPath(Constants.PLUGIN_ID,
        getEventPath());
    file = new File(eventPath);
    RandomAccessFile eventFile = new RandomAccessFile(eventPath, "r");

    eventName = file.getName();
    IEventParser eventParser = eventParserFactory.create(eventName, eventFile);

    IEventServiceFactory eventFactory = injector
        .getInstance(IEventServiceFactory.class);

    EventLoadOptions loadOptions = new EventLoadOptions();
    loadOptions.setEventsLoadCount(getEventCount());
    loadOptions.setEnableLoopEvents(isLoopsEnabled());

    eventService = (IEventServiceExtended) eventFactory.create(manager,
        eventParser, metaService, loadOptions);
  }

  @After
  public void teardown() throws IOException {
    metaService.close();
    eventService.close();
  }

  abstract String getMetaPath();

  abstract String getEventPath();

  protected int getEventCount() {
    return EventLoadOptions.DEFAULT_EVENTS_COUNT;
  }

  protected boolean isLoopsEnabled() {
    return false;
  }

  public static void assertEvent(InvocationEventType type, long threadId,
      long methodMetaId, IInvocationEvent event) {
    IMethodInvocationEvent methodEvent = (IMethodInvocationEvent) event;
    Assert.assertEquals(type, methodEvent.getType());
    Assert.assertEquals(threadId, methodEvent.getThread().getId());
    Assert.assertEquals(methodMetaId, methodEvent.getMethod().getMetaId()
        .longValue());
  }

  public static void assertLoopEvent(long threadId, IInvocationEvent event,
      final int count, IInvocationEvent... expectedLoopEvents) {
    InvocationLoopEvent loopEvent = (InvocationLoopEvent) event;
    Assert.assertEquals(count, loopEvent.getLoopCount());
    List<IInvocationEvent> loopEvents = loopEvent.getEvents();
    Assert.assertTrue(expectedLoopEvents.length > 0);
    Assert.assertEquals(expectedLoopEvents.length, loopEvents.size());
    for (int i = 0; i < expectedLoopEvents.length; i++) {
      Assert.assertTrue(EVENT_COMPARATOR.compare(loopEvents.get(i),
          expectedLoopEvents[i]));
    }
  }

  public static void assertTraceEvent(IInvocationEvent event,
      InvocationEventType type, long threadId, String[] traceData) {
    IMethodTraceInvocationEvent traceEvent = (IMethodTraceInvocationEvent) event;
    Assert.assertEquals(type, traceEvent.getType());
    Assert.assertEquals(threadId, traceEvent.getThread().getId());
    List<IMethod> trace = traceEvent.getMethodTrace();
    Assert.assertEquals(traceData.length, trace.size() * 3);

    for (int i = 0; i < trace.size(); i++) {
      IMethod method = trace.get(i);
      Assert.assertEquals("L" + traceData[i * 3] + ";", method.getIClass()
          .getCanonicalName());
      Assert.assertEquals(traceData[i * 3 + 1], method.getName());
      Assert.assertEquals(traceData[i * 3 + 2],
          method.getCanonicalDescription());
    }
  }
}
