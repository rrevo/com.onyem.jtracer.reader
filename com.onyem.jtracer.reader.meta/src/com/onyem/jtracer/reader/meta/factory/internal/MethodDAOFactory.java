package com.onyem.jtracer.reader.meta.factory.internal;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;
import com.onyem.jtracer.reader.meta.internal.dao.MethodDAO;

public interface MethodDAOFactory {

  MethodDAO create(IMetaServiceExtended metaService,
      @Assisted IConnectionManager connectionManager);
}
