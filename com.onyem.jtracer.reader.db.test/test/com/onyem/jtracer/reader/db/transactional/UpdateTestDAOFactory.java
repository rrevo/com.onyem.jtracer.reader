package com.onyem.jtracer.reader.db.transactional;

import com.onyem.jtracer.reader.db.IJdbcHelper;

public interface UpdateTestDAOFactory {

  UpdateTestDAO create(IJdbcHelper helper);

}
