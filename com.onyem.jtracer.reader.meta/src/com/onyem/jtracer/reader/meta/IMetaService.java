package com.onyem.jtracer.reader.meta;

import java.io.Closeable;

public interface IMetaService extends Closeable {

  IClass getClassById(long id);

  IClass getClassByMetaId(long metaId);

  IClass getPlainClassByName(String name);

  IMethod getMethodById(long id);

  IMethod getMethodByMetaId(long metaId);

  IMethod getMethodByNameDescription(String methodName, String methodSignature,
      IClass clazz);
}
