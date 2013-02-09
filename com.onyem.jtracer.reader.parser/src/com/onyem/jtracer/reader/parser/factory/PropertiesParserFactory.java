package com.onyem.jtracer.reader.parser.factory;

import java.io.File;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.parser.IPropertiesParser;

public interface PropertiesParserFactory {

  IPropertiesParser create(@Assisted File file);
}
