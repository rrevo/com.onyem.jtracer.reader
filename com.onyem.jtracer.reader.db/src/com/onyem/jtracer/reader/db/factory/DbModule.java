package com.onyem.jtracer.reader.db.factory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.Transactional;
import com.onyem.jtracer.reader.db.internal.ConnectionManagerFactory;
import com.onyem.jtracer.reader.db.internal.JdbcHelper;
import com.onyem.jtracer.reader.db.internal.JdbcTransactionInterceptor;

public class DbModule extends AbstractModule {

  @Override
  protected void configure() {
    bindInterceptor(Matchers.any(),
        Matchers.annotatedWith(Transactional.class),
        new JdbcTransactionInterceptor());

    bind(IConnectionManagerFactory.class).to(ConnectionManagerFactory.class);

    install(new FactoryModuleBuilder().implement(IJdbcHelper.class,
        JdbcHelper.class).build(IJdbcHelperFactory.class));
  }

}
