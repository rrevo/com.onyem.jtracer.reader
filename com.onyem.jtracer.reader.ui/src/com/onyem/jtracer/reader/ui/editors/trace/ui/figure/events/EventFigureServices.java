package com.onyem.jtracer.reader.ui.editors.trace.ui.figure.events;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.ui.IImageManager;

@Immutable
class EventFigureServices {

  final IImageManager imageManager;
  final IMetaService metaService;

  public EventFigureServices(IImageManager imageManager,
      IMetaService metaService) {
    this.imageManager = imageManager;
    this.metaService = metaService;
  }

}
