package com.onyem.jtracer.reader.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.onyem.jtracer.reader.db.factory.DbModule;
import com.onyem.jtracer.reader.events.factory.EventModule;
import com.onyem.jtracer.reader.meta.factory.MetaModule;
import com.onyem.jtracer.reader.parser.factory.ParserModule;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.queue.factory.QueueModule;
import com.onyem.jtracer.reader.ui.factory.TraceFactory;
import com.onyem.jtracer.reader.ui.factory.UiModule;

public class Activator implements BundleActivator {

  public static final String PLUGIN_ID = "com.onyem.jtracer.reader.ui";

  public static BundleContext context;
  private static ILog LOG;
  private static IQueueService queueService;

  private static TraceFactory traceFactory;

  // Cannot instantiate the ImageManager when the bundle is loading
  private static Provider<IImageManager> imageManagerProvider;

  public void start(BundleContext context) throws Exception {
    Activator.context = context;
    LOG = Platform.getLog(context.getBundle());

    Injector injector = Guice
        .createInjector(new QueueModule(), new DbModule(), new MetaModule(),
            new ParserModule(), new EventModule(), new UiModule());
    queueService = injector.getInstance(IQueueService.class);
    traceFactory = injector.getInstance(TraceFactory.class);
    imageManagerProvider = injector.getProvider(IImageManager.class);
  }

  public void stop(BundleContext context) throws Exception {
    queueService.close();
    queueService = null;
    traceFactory = null;
    imageManagerProvider = null;
    LOG = null;
    context = null;
  }

  public static IQueueService getQueue() {
    return queueService;
  }

  public static TraceFactory getTraceFactory() {
    return traceFactory;
  }

  public static IImageManager getImageManager() {
    return imageManagerProvider.get();
  }

  public static void logInfo(String message, Throwable throwable) {
    LOG.log(createStatus(IStatus.INFO, message, throwable));
  }

  public static void logError(String message, Throwable throwable) {
    LOG.log(createStatus(IStatus.ERROR, message, throwable));
  }

  private static IStatus createStatus(int severity, String message,
      Throwable throwable) {
    return new Status(severity, Activator.PLUGIN_ID, IStatus.OK, message,
        throwable);
  }

}
