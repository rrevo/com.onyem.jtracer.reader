package com.onyem.jtracer.reader.meta;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  private static final String PLUGIN_ID = "com.onyem.jtracer.reader.meta";

  public static BundleContext context;
  private static ILog LOG;

  public void start(BundleContext context) throws Exception {
    Activator.context = context;
    LOG = Platform.getLog(context.getBundle());
  }

  public void stop(BundleContext context) throws Exception {
    LOG = null;
    context = null;
  }

  public static void logInfo(String message, Throwable throwable) {
    LOG.log(createStatus(IStatus.INFO, message, throwable));
  }

  public static void logError(String message, Throwable throwable) {
    LOG.log(createStatus(IStatus.ERROR, message, throwable));
  }

  private static IStatus createStatus(int severity, String message,
      Throwable throwable) {
    return new Status(severity, PLUGIN_ID, IStatus.OK, message, throwable);
  }

}
