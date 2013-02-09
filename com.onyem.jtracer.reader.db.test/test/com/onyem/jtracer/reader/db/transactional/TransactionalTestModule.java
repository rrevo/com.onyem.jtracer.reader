package com.onyem.jtracer.reader.db.transactional;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TransactionalTestModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().implement(UpdateTestDAO.class,
        UpdateTestDAO.class).build(UpdateTestDAOFactory.class));

    install(new FactoryModuleBuilder().implement(UpdateReturnTestDAO.class,
        UpdateReturnTestDAO.class).build(UpdateReturnTestDAOFactory.class));

    install(new FactoryModuleBuilder().implement(ExecuteTestDAO.class,
        ExecuteTestDAO.class).build(ExecuteTestDAOFactory.class));
  }
}
