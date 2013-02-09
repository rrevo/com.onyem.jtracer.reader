package com.onyem.jtracer.reader.parser.factory;

import java.io.RandomAccessFile;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.parser.IEventParser;

public interface EventParserFactory {

  IEventParser create(@Assisted String name, @Assisted RandomAccessFile metaFile);

}
