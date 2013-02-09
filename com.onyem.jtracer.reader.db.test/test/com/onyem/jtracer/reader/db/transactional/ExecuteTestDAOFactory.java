package com.onyem.jtracer.reader.db.transactional;

import com.onyem.jtracer.reader.db.IJdbcHelper;

public interface ExecuteTestDAOFactory {

  ExecuteTestDAO create(IJdbcHelper helper);

}
