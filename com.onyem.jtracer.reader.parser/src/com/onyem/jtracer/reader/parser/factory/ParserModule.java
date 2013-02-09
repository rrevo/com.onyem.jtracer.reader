package com.onyem.jtracer.reader.parser.factory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.onyem.jtracer.reader.parser.IEventParser;
import com.onyem.jtracer.reader.parser.IMetaParser;
import com.onyem.jtracer.reader.parser.IPropertiesParser;
import com.onyem.jtracer.reader.parser.internal.EventParser;
import com.onyem.jtracer.reader.parser.internal.MetaParser;
import com.onyem.jtracer.reader.parser.internal.PropertiesParser;

public class ParserModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().implement(IPropertiesParser.class,
        PropertiesParser.class).build(PropertiesParserFactory.class));

    install(new FactoryModuleBuilder().implement(IMetaParser.class,
        MetaParser.class).build(MetaParserFactory.class));

    install(new FactoryModuleBuilder().implement(IEventParser.class,
        EventParser.class).build(EventParserFactory.class));
  }
}
