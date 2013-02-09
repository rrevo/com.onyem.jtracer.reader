package com.onyem.jtracer.reader.meta.internal;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.meta.IMetaService;
import com.onyem.jtracer.reader.meta.factory.IMetaServiceFactory;
import com.onyem.jtracer.reader.meta.factory.internal.ClassDAOFactory;
import com.onyem.jtracer.reader.meta.factory.internal.MethodDAOFactory;
import com.onyem.jtracer.reader.meta.internal.dao.ClassDAO;
import com.onyem.jtracer.reader.meta.internal.dao.MethodDAO;
import com.onyem.jtracer.reader.meta.internal.parser.MetaParserHelper;
import com.onyem.jtracer.reader.parser.IMetaParser;

public class MetaServiceFactory implements IMetaServiceFactory {

  private final Provider<ClassNameUtils> nameUtilsProvider;
  private final Provider<MetaParserHelper> metaParserHelperProvider;
  private final Provider<ClassDAOFactory> classDaoFactoryProvider;
  private final Provider<MethodDAOFactory> methodDaoFactoryProvider;

  @Inject
  MetaServiceFactory(Provider<ClassNameUtils> nameUtilsProvider,
      Provider<MetaParserHelper> metaParserHelperProvider,
      Provider<ClassDAOFactory> classDaoFactoryProvider,
      Provider<MethodDAOFactory> methodDaoFactoryProvider) {
    this.nameUtilsProvider = nameUtilsProvider;
    this.metaParserHelperProvider = metaParserHelperProvider;
    this.classDaoFactoryProvider = classDaoFactoryProvider;
    this.methodDaoFactoryProvider = methodDaoFactoryProvider;
  }

  @Override
  public IMetaService create(IConnectionManager connectionManager,
      IMetaParser metaParser) {
    MetaService metaService = new MetaService(nameUtilsProvider.get(),
        metaParserHelperProvider.get(), metaParser);

    ClassDAOFactory classDAOFactory = classDaoFactoryProvider.get();
    ClassDAO classDAO = classDAOFactory.create(metaService, connectionManager);
    metaService.setClassDAO(classDAO);

    MethodDAOFactory methodDAOFactory = methodDaoFactoryProvider.get();
    MethodDAO methodDAO = methodDAOFactory.create(metaService,
        connectionManager);
    metaService.setMethodDAO(methodDAO);

    return metaService;
  }
}
