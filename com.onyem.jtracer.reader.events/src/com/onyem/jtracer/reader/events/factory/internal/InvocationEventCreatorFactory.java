package com.onyem.jtracer.reader.events.factory.internal;

import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.events.internal.InvocationEventCreator;
import com.onyem.jtracer.reader.meta.IMetaService;

public interface InvocationEventCreatorFactory {

  InvocationEventCreator create(@Assisted IMetaService metaService);
}
