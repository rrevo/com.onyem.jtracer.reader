package com.onyem.jtracer.reader.meta.factory;

import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.parser.IMetaParser;

public interface IMetaServiceFactory {

  public IMetaService create(IConnectionManager connectionManager,
      IMetaParser metaParser);
}
