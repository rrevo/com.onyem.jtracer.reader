package com.onyem.jtracer.reader.db.transactional;

import com.onyem.jtracer.reader.db.IJdbcHelper;

public interface UpdateReturnTestDAOFactory {

  UpdateReturnTestDAO create(IJdbcHelper helper);

}
