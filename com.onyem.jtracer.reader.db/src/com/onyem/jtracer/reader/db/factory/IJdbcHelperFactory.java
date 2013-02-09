package com.onyem.jtracer.reader.db.factory;

import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;

public interface IJdbcHelperFactory {

  IJdbcHelper create(IConnectionManager connectionManager);

}
