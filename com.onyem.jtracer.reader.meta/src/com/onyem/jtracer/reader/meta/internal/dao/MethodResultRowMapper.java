package com.onyem.jtracer.reader.meta.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;
import com.onyem.jtracer.reader.meta.internal.MethodImpl;

@NotThreadSafe
class MethodResultRowMapper implements ResultRowMapper<IMethod> {

  private final IMetaServiceExtended metaService;
  private List<ClassId> parameters = new ArrayList<ClassId>();
  private List<ClassId> exceptions = new ArrayList<ClassId>();

  MethodResultRowMapper(IMetaServiceExtended metaService) {
    this.metaService = metaService;
  }

  public void setParameters(List<ClassId> parameters) {
    this.parameters = parameters;
  }

  public void setExceptions(List<ClassId> exceptions) {
    this.exceptions = exceptions;
  }

  @Override
  public IMethod mapRow(ResultSet rs, int rowNum) throws SQLException {
    Long id = rs.getLong("ID");
    Long metaId = rs.getLong("META_ID");
    if (rs.wasNull()) {
      metaId = null;
    }
    Integer access = rs.getInt("ACCESS");
    if (rs.wasNull()) {
      access = null;
    }
    String name = rs.getString("NAME");

    long classId = rs.getLong("CLASS_ID");
    ClassId clazz = metaService.getClassIdById(classId);

    long returnTypeId = rs.getLong("RETURN_ID");
    ClassId returnType = metaService.getClassIdById(returnTypeId);

    String description = rs.getString("DESCRIPTION");
    String signature = rs.getString("SIGNATURE");
    if (rs.wasNull()) {
      signature = null;
    }

    IMethod method = MethodImpl.createMethod(id, metaId, access, name, clazz,
        parameters, returnType, exceptions, description, signature);
    return method;
  }
}
