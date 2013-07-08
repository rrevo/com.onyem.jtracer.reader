package com.onyem.jtracer.reader.meta.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.ClassType;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.internal.ClassIdImpl;
import com.onyem.jtracer.reader.meta.internal.ClassImpl;
import com.onyem.jtracer.reader.meta.internal.ClassNameUtils;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;

@NotThreadSafe
class ClassResultRowMapper implements ResultRowMapper<IClass> {

  private final IMetaServiceExtended metaService;
  private final ClassNameUtils nameUtils;

  private Set<ClassId> interfaces = null;

  ClassResultRowMapper(IMetaServiceExtended metaService,
      ClassNameUtils nameUtils) {
    this.metaService = metaService;
    this.nameUtils = nameUtils;
  }

  public void setInterfaces(Set<ClassId> interfaces) {
    this.interfaces = interfaces;
  }

  @Override
  public IClass mapRow(ResultSet rs, int rowNum) throws SQLException {
    Long id = rs.getLong("ID");
    Long metaId = rs.getLong("META_ID");
    if (rs.wasNull()) {
      metaId = null;
    }
    Integer access = rs.getInt("ACCESS");
    if (rs.wasNull()) {
      access = null;
    }
    String className = rs.getString("NAME");
    String packageName = rs.getString("PACKAGE");
    String signature = rs.getString("SIGNATURE");

    boolean isClass = rs.getBoolean("IS_CLASS");
    boolean isInterface = rs.getBoolean("IS_INTERFACE");
    boolean isPrimitive = rs.getBoolean("IS_PRIMITIVE");
    boolean isArray = rs.getBoolean("IS_ARRAY");

    Long superClassId = rs.getLong("SUPERCLASS_ID");
    ClassId superClazz = null;
    if (!rs.wasNull()) {
      superClazz = new ClassIdImpl(superClassId);
    }

    Long componentId = rs.getLong("COMPONENT_ID");
    IClass componentClazz = null;
    if (!rs.wasNull()) {
      componentClazz = metaService.getClassById(componentId);
    }

    // In the DB, voids are stored as IS_PRIMITIVE
    boolean isVoid = isPrimitive && nameUtils.isVoid(className, false);
    isPrimitive = isVoid ? false : isPrimitive;

    String canonicalName = nameUtils.getCanonicalClassName(isClass,
        isInterface, isPrimitive, isVoid, isArray, className, packageName,
        componentClazz);

    ClassType classType = getClassType(isClass, isInterface, isPrimitive,
        isVoid, isArray);
    IClass clazz = ClassImpl.create(id, metaId, access, className, packageName,
        classType, superClazz, interfaces, componentClazz, signature,
        canonicalName);
    return clazz;
  }

  private ClassType getClassType(boolean isClass, boolean isInterface,
      boolean isPrimitive, boolean isVoid, boolean isArray) {
    int count = 0;
    ClassType classType = null;
    if (isClass) {
      count++;
      classType = ClassType.CLASS;
    }
    if (isInterface) {
      count++;
      classType = ClassType.INTERFACE;
    }
    if (isPrimitive) {
      count++;
      classType = ClassType.PRIMITIVE;
    }
    if (isVoid) {
      count++;
      classType = ClassType.VOID;
    }
    if (isArray) {
      count++;
      classType = ClassType.ARRAY;
    }
    assert count == 1;
    return classType;
  }
}
