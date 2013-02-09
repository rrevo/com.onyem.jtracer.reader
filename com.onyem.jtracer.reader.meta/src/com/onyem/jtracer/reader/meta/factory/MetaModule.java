package com.onyem.jtracer.reader.meta.factory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.onyem.jtracer.reader.meta.factory.internal.ClassDAOFactory;
import com.onyem.jtracer.reader.meta.factory.internal.MethodDAOFactory;
import com.onyem.jtracer.reader.meta.internal.MetaServiceFactory;
import com.onyem.jtracer.reader.meta.internal.dao.ClassDAO;
import com.onyem.jtracer.reader.meta.internal.dao.MethodDAO;

public class MetaModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IMetaServiceFactory.class).to(MetaServiceFactory.class);

    install(new FactoryModuleBuilder()
        .implement(ClassDAO.class, ClassDAO.class).build(ClassDAOFactory.class));

    install(new FactoryModuleBuilder().implement(MethodDAO.class,
        MethodDAO.class).build(MethodDAOFactory.class));
  }
}
