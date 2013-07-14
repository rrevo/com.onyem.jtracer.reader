package com.onyem.jtracer.reader.ui.editors.trace.model;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.concurrent.ThreadSafe;

import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.factory.IConnectionManagerFactory;
import com.onyem.jtracer.reader.db.factory.IJdbcHelperFactory;
import com.onyem.jtracer.reader.events.IEventService;
import com.onyem.jtracer.reader.events.factory.IEventServiceFactory;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.factory.IMetaServiceFactory;
import com.onyem.jtracer.reader.parser.IEventParser;
import com.onyem.jtracer.reader.parser.IMetaParser;
import com.onyem.jtracer.reader.parser.IPropertiesParser;
import com.onyem.jtracer.reader.parser.PropertyKeys;
import com.onyem.jtracer.reader.parser.factory.EventParserFactory;
import com.onyem.jtracer.reader.parser.factory.MetaParserFactory;
import com.onyem.jtracer.reader.parser.factory.PropertiesParserFactory;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.ClassNameRule;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.ClassTraceCheckerFactory;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.IRuleClassTraceChecker;
import com.onyem.jtracer.reader.ui.util.Constants;

@ThreadSafe
public class Trace implements Closeable {

  private final String applicationPath;
  private final IPropertiesParser propertiesParser;

  private final IConnectionManager connectionManager;
  private final IJdbcHelperFactory jdbcHelperFactory;
  private final IMetaService metaService;
  private final Map<String, IEventService> eventServiceMap;

  private final Map<String, Long> triggerFiles;
  private final IRuleClassTraceChecker classTraceChecker;

  public Trace(PropertiesParserFactory propertiesParserFactory,
      IConnectionManagerFactory connectionManagerFactory,
      IJdbcHelperFactory jdbcHelperFactory,
      MetaParserFactory metaParserFactory,
      IMetaServiceFactory metaServiceFactory,
      EventParserFactory eventParserFactory,
      IEventServiceFactory eventServiceFactory, IQueueService queueService,
      String applicationPath) {
    this.applicationPath = applicationPath;

    File jtraceFile = new File(applicationPath, Constants.TRACE_FILE_NAME);
    this.propertiesParser = propertiesParserFactory.create(jtraceFile);

    this.connectionManager = connectionManagerFactory
        .createWithMigration(getDatabasePath());
    this.jdbcHelperFactory = jdbcHelperFactory;

    File metaFile = new File(applicationPath, Constants.META_FILE_NAME);
    RandomAccessFile randomMetaFile = null;
    try {
      randomMetaFile = new RandomAccessFile(metaFile, "r");
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(e);
    }

    IMetaParser metaParser = metaParserFactory.create(metaFile.getName(),
        randomMetaFile);
    this.metaService = metaServiceFactory.create(connectionManager, metaParser);

    this.triggerFiles = createTriggerEventFileNames();
    this.classTraceChecker = createClassChecker();
    this.eventServiceMap = createEventServices(eventParserFactory,
        eventServiceFactory, triggerFiles.keySet());

    // Get some events from the 0th eventService
    validate(eventServiceMap.get(eventServiceMap.keySet().iterator().next()));

    loadEvents(eventServiceMap, queueService);
  }

  private String getDatabasePath() {
    return applicationPath + File.separator + Constants.DATABASE_PATH;
  }

  private Map<String, Long> createTriggerEventFileNames() {
    List<String> eventFiles = propertiesParser
        .getMultiValue(PropertyKeys.EVENT_FILE);

    // Parse for the trigger event files
    // Ordering of the events is important
    Map<String, Long> triggerFiles = new LinkedHashMap<String, Long>();
    Pattern triggerFilePattern = Pattern.compile("(\\d*)::(.*)");
    for (String eventFile : eventFiles) {
      Matcher matcher = triggerFilePattern.matcher(eventFile);
      if (matcher.matches()) {
        long methodIndex = Long.parseLong(matcher.group(1));
        String fileName = matcher.group(2);
        triggerFiles.put(fileName, methodIndex);
      }
    }
    return Collections.unmodifiableMap(triggerFiles);
  }

  private IRuleClassTraceChecker createClassChecker() {
    String param = propertiesParser.getValue(PropertyKeys.SELECTOR);
    String[] rulesAsString = param.split(",");
    Set<ClassNameRule> rules = new HashSet<ClassNameRule>();
    for (String ruleAsString : rulesAsString) {
      ClassNameRule rule = ClassNameRule.createRule(ruleAsString);
      rules.add(rule);
    }
    return ClassTraceCheckerFactory.createClassTraceChecker(rules);
  }

  private Map<String, IEventService> createEventServices(
      EventParserFactory eventParserFactory,
      IEventServiceFactory eventServiceFactory, Set<String> eventFiles) {
    Map<String, IEventService> eventServices = new HashMap<String, IEventService>();
    if (eventFiles.isEmpty()) {
      eventServices.put(
          Constants.EVENT_FILE_NAME,
          createEventService(eventParserFactory, eventServiceFactory,
              Constants.EVENT_FILE_NAME));
    } else {
      for (String eventFile : eventFiles) {
        eventServices.put(
            eventFile,
            createEventService(eventParserFactory, eventServiceFactory,
                eventFile));
      }
    }
    return Collections.unmodifiableMap(eventServices);
  }

  private IEventService createEventService(
      EventParserFactory eventParserFactory,
      IEventServiceFactory eventServiceFactory, String eventFileName) {
    RandomAccessFile eventFile = null;
    try {
      eventFile = new RandomAccessFile(getApplicationPath() + File.separator
          + eventFileName, "r");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    IEventParser eventParser = eventParserFactory.create(eventFileName,
        eventFile);
    IEventService eventService = eventServiceFactory.create(connectionManager,
        eventParser, metaService);
    return eventService;
  }

  /*
   * Basic check for this Trace object
   */
  private void validate(IEventService eventService) {
    eventService.getNextEvent(null);
  }

  private void loadEvents(Map<String, IEventService> eventServiceMap,
      IQueueService queueService) {
    for (IEventService eventService : eventServiceMap.values()) {
      eventService.loadEvents(queueService);
    }
  }

  public String getApplicationPath() {
    return applicationPath;
  }

  public Map<String, Long> getTriggerEventFileNames() {
    return triggerFiles;
  }

  public IRuleClassTraceChecker getClassTraceChecker() {
    return classTraceChecker;
  }

  public List<String[]> getTriggerData() {
    String triggerParams = propertiesParser.getValue(PropertyKeys.TRIGGER);
    List<String[]> triggers = new ArrayList<String[]>();
    if (triggerParams != null) {
      String[] classTriggers = triggerParams.split("}");
      for (String classTrigger : classTriggers) {
        int curlyIndex = classTrigger.indexOf("{");
        if (curlyIndex > 0) {
          String className = classTrigger.substring(0, curlyIndex).trim();
          if (className.startsWith(",")) {
            className = className.substring(1).trim();
          }
          String methodTriggers = classTrigger.substring(curlyIndex + 1,
              classTrigger.length());
          String[] methodNames = methodTriggers.split(",");
          for (int i = 0; i < methodNames.length; i++) {
            triggers.add(new String[] { className, methodNames[i].trim() });
          }
        }
      }
    }
    return triggers;
  }

  public List<String[]> getProperties() {
    List<String[]> properties = new ArrayList<String[]>();
    properties.add(new String[] { "Path", getApplicationPath() });
    String paramStartTime = propertiesParser.getValue(PropertyKeys.START_TIME);
    if (paramStartTime != null) {
      try {
        long startTime = Long.parseLong(paramStartTime);
        String startTimeString = DateFormat.getDateTimeInstance().format(
            new Date(startTime));
        properties.add(new String[] { "Start Time", startTimeString });
      } catch (NumberFormatException e) {
        // Ignore the exception
      }
    }
    return properties;
  }

  public List<String[]> getOtherProperties() {
    String[] propertyKeys = new String[] { PropertyKeys.EVENT_FILE,
        PropertyKeys.SELECTOR, PropertyKeys.TRIGGER, PropertyKeys.START_TIME };
    List<String[]> properties = new ArrayList<String[]>();
    for (String key : propertiesParser.getKeys()) {
      boolean found = false;
      for (String propertyKey : propertyKeys) {
        if (key.equalsIgnoreCase(propertyKey)) {
          found = true;
          break;
        }
      }
      if (!found) {
        properties.add(new String[] { key, propertiesParser.getValue(key) });
      }
    }
    return properties;
  }

  public IMetaService getMetaService() {
    return metaService;
  }

  public IEventService getEventService(String eventFileName) {
    return eventServiceMap.get(eventFileName);
  }

  public IJdbcHelper getJdbcHelper() {
    return jdbcHelperFactory.create(connectionManager);
  }

  @Override
  public void close() throws IOException {
    propertiesParser.close();
    connectionManager.closeDatabase();
    metaService.close();
    for (String eventFile : eventServiceMap.keySet()) {
      IEventService eventService = eventServiceMap.get(eventFile);
      eventService.close();
    }

  }
}
