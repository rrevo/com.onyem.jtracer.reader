package com.onyem.jtracer.reader.db.internal;

import java.sql.PreparedStatement;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.db.ParameterSource;

@Immutable
public class NullParameterSource implements ParameterSource {

  @Override
  public void setParameters(PreparedStatement st) {
    // do nothing
  }

}
