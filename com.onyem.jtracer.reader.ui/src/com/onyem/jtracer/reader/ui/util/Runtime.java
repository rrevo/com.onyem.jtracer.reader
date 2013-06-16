package com.onyem.jtracer.reader.ui.util;

import java.io.File;

import org.eclipse.core.runtime.Platform;

public class Runtime {
  private Runtime() {
  }

  public enum OperatingSystem {
    WINDOWS, LINUX
  }

  public static String getDefaultAgentPath() {
    String platformPath = Platform.getInstallLocation().getURL().getFile();
    File platformFile = new File(platformPath);
    if (platformFile.exists()) {
      String agentPath = platformFile.getParentFile().getAbsolutePath()
          + File.separator + Constants.AGENT_FOLDER + File.separator
          + getAgentLibName(getOperatingSystem());
      File agentFile = new File(agentPath);
      if (agentFile.exists()) {
        return agentFile.getAbsolutePath();
      }
    }
    return null;
  }

  public static File getSamplePath(String sampleName) {
    String platformPath = Platform.getInstallLocation().getURL().getFile();
    File platformFile = new File(platformPath);
    if (platformFile.exists()) {
      String samplePath = platformFile.getParentFile().getAbsolutePath()
          + File.separator + Constants.SAMPLES_FOLDER + File.separator
          + sampleName + File.separator + Constants.SAMPLES_LOGS_FOLDER
          + File.separator + Constants.TRACE_FILE_NAME;
      File sampleFile = new File(samplePath);
      if (sampleFile.exists()) {
        return sampleFile;
      }
    }
    return null;
  }

  public static OperatingSystem getOperatingSystem() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.indexOf("win") >= 0) {
      return OperatingSystem.WINDOWS;

    }
    if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
      return OperatingSystem.LINUX;
    }
    throw new IllegalArgumentException();
  }

  public static String getAgentLibName(OperatingSystem os) {
    switch (os) {
    case WINDOWS:
      return "jtracer-agent.dll";
    case LINUX:
      return "libjtracer-agent.so";

    default:
      throw new IllegalArgumentException();
    }

  }
}
