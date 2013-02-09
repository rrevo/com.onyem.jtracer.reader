package com.onyem.jtracer.reader.ui.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.internal.ImageManager;
import com.onyem.jtracer.reader.ui.internal.factory.TraceFactoryImpl;

public class UiModule extends AbstractModule {

  @Override
  protected void configure() {

    bind(IImageManager.class).to(ImageManager.class).in(Singleton.class);

    bind(TraceFactory.class).to(TraceFactoryImpl.class);
  }
}
