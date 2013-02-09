package com.onyem.jtracer.reader.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.concurrent.Immutable;

@Immutable
public class URLDownloader {

  private URLDownloader() {
  }

  private static final int BUFFER_SIZE = 8 * 1024;

  public static String download(String address) {
    InputStream is = null;
    StringBuilder builder = new StringBuilder();
    byte[] buffer = new byte[BUFFER_SIZE];
    try {
      int bytesRead = 0;
      URL url = new URL(address);
      URLConnection connection = url.openConnection();
      is = connection.getInputStream();
      while ((bytesRead = is.read(buffer)) != -1) {
        String data = new String(buffer, 0, bytesRead);
        builder.append(data);
      }
      return builder.toString();
    } catch (MalformedURLException e) {
      return null;
    } catch (IOException e) {
      return null;
    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }
  }
}
