package com.onyem.jtracer.reader.events.factory.internal;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.events.internal.dao.ThreadDAO;

public interface ThreadDAOFactory {

  ThreadDAO create(@Assisted IConnectionManager connectionManager);
}
