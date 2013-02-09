package com.onyem.jtracer.reader.parser.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.parser.IPropertiesParser;
import com.onyem.jtracer.reader.utils.FileUtils;

@Service
@Immutable
public class PropertiesParser implements IPropertiesParser {

  private final List<Property> properties;

  @Inject
  protected PropertiesParser(@Assisted File file) {
    String contents = FileUtils.getContents(file);
    properties = Collections.unmodifiableList(parse(contents));
  }

  private List<Property> parse(String contents) {
    List<Property> properties = new ArrayList<Property>();
    BufferedReader reader = new BufferedReader(new StringReader(contents));
    try {
      String line = reader.readLine();
      while (line != null) {
        line = line.trim();
        if (!line.isEmpty() && !line.startsWith("#")) {
          int equalIndex = line.indexOf("=");
          if (equalIndex != -1) {
            String key = line.substring(0, equalIndex).trim();
            String value = line.substring(equalIndex + 1).trim();
            if (value.isEmpty()) {
              value = null;
            }
            properties.add(new Property(key, value));
          } else {
            properties.add(new Property(line, null));
          }
        }
        line = reader.readLine();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  @Override
  public List<String> getKeys() {
    List<String> keys = new ArrayList<String>();
    for (Property p : properties) {
      keys.add(p.getKey());
    }
    return keys;
  }

  @Override
  public boolean isKey(String key) {
    for (Property p : properties) {
      if (p.getKey().equals(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getValue(String key) {
    for (Property p : properties) {
      if (p.getKey().equals(key)) {
        return p.getValue();
      }
    }
    return null;
  }

  @Override
  public List<String> getMultiValue(String key) {
    List<String> values = new ArrayList<String>();
    for (Property p : properties) {
      if (p.getKey().equals(key)) {
        values.add(p.getValue());
      }
    }
    return values;
  }

  @Override
  public void close() throws IOException {
  }
}
