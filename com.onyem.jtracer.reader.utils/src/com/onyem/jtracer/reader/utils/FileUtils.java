package com.onyem.jtracer.reader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.concurrent.Immutable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

@Immutable
public class FileUtils {

  private FileUtils() {
  }

  public static String getPluginPath(String pluginId, String relativePath) {
    String base = Platform.getBundle(pluginId).getEntry("/").toString();
    URL fileURL;
    try {
      fileURL = FileLocator.toFileURL(new URL(base + relativePath));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileURL.getPath();
  }

  public static String getContents(File file) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file)));
      StringBuilder builder = new StringBuilder();
      String line = reader.readLine();
      while (line != null) {
        builder.append(line).append("\n");
        line = reader.readLine();
      }
      return builder.toString();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static String getTempPath() {
    return getTempPath("onyem");
  }

  public static String getTempPath(String prefix) {
    File tmp;
    try {
      tmp = File.createTempFile(prefix, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String path = tmp.getAbsolutePath();
    tmp.delete();
    return path;
  }

  public static void deleteFile(String path) {
    File file = new File(path);
    if (file.isDirectory()) {
      for (File childFile : file.listFiles()) {
        deleteFile(childFile.getAbsolutePath());
      }
    }
    file.delete();
  }

}
