package com.onyem.jtracer.reader.parser.factory;

import java.io.RandomAccessFile;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.parser.IMetaParser;

public interface MetaParserFactory {

  IMetaParser create(@Assisted String name, @Assisted RandomAccessFile metaFile);
}
