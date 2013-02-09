package com.onyem.jtracer.reader.ui.util;

public interface Messages {

  public String VERSION_UNKNOWN = "Check for updates at <A>"
      + Constants.URL_DOWNLOAD + "</A>";

  public String VERSION_NEW_AVAILABLE = "Download a new version at <A>"
      + Constants.URL_DOWNLOAD + "</A>";

  public String VERSION_AT_LATEST = "Version " + Constants.AGENT_VERSION;

  public final String SUMMARY_LABEL = "Summary";
  public final String TRACE_LABEL = "Trace";
  public final String OPEN_LABEL = "Open";
  public final String LOADING_LABEL = "Loading ...";

  public String ERROR_HEADER = "Error";

  public String CLOSE = "Close";
  public String OK = "OK";

  public String ERROR_OPENING_TRACE = "Error opening trace";
  public String ERROR_OPENING_HELP = "Error opening Help";
  //  public String ERROR_OPENING_CONFIG_EDITOR = "Error opening Agent Configuration Editor";

  public String ERROR_CLOSING_TRACE = "Error closing trace";

}
