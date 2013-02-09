package com.onyem.jtracer.reader.meta.internal.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.DAO;
import com.onyem.jtracer.reader.db.IConnectionManager;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.PreparedStatementCreator;
import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.db.Transactional;
import com.onyem.jtracer.reader.db.factory.IJdbcHelperFactory;
import com.onyem.jtracer.reader.db.util.LongParameterSource;
import com.onyem.jtracer.reader.db.util.LongResultRowMapper;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;

@DAO
@Immutable
public class MethodDAO {

  private final IMetaServiceExtended metaService;
  private final IJdbcHelper helper;

  @Inject
  public MethodDAO(IJdbcHelperFactory helperFactory,
      @Assisted IMetaServiceExtended metaService,
      @Assisted IConnectionManager connectionManager) {
    this.metaService = metaService;
    helper = helperFactory.create(connectionManager);
  }

  @Transactional
  public IMethod getMethodByMetaId(final long metaId) {
    return getMethodBy(metaId, "M.META_ID = ?");
  }

  @Transactional
  public IMethod getMethodById(long id) {
    return getMethodBy(id, "M.ID = ?");
  }

  private IMethod getMethodBy(long idValue, String whereClause) {
    ParameterSource parameterSource = new LongParameterSource(idValue);
    MethodResultRowMapper methodResultRowMapper = new MethodResultRowMapper(
        metaService);

    List<Long> parameterIds = helper.query(getParametersQuery() + whereClause,
        parameterSource, new LongResultRowMapper());
    methodResultRowMapper.setParameters(getClasses(parameterIds));

    List<Long> exceptionIds = helper.query(getExceptionsQuery() + whereClause,
        parameterSource, new LongResultRowMapper());
    methodResultRowMapper.setExceptions(getClasses(exceptionIds));

    IMethod method = helper.queryForObject("SELECT * FROM METHODS M WHERE "
        + whereClause, parameterSource, methodResultRowMapper);
    return method;
  }

  private String getParametersQuery() {
    return "SELECT MP.PARAMETER_ID "
        + " FROM METHOD_PARAMETERS MP JOIN METHODS M ON MP.METHOD_ID = M.ID "
        + " WHERE ";
  }

  private String getExceptionsQuery() {
    return "SELECT ME.EXCEPTION_ID "
        + " FROM METHOD_EXCEPTIONS ME JOIN METHODS M ON ME.METHOD_ID = M.ID "
        + " WHERE ";
  }

  private List<IClass> getClasses(List<Long> classIds) {
    List<IClass> classes = new ArrayList<IClass>();
    for (long classId : classIds) {
      classes.add(metaService.getClassById(classId));
    }
    return classes;
  }

  @Transactional
  public IMethod insertMethod(final IMethod method) {
    final long id = helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        Long metaId = method.getMetaId();
        int index = 1;
        if (metaId == null) {
          statement.setNull(index++, Types.BIGINT);
        } else {
          statement.setLong(index++, metaId);
        }
        statement.setString(index++, method.getName());

        Integer access = method.getAccess();
        if (access == null) {
          statement.setNull(index++, Types.INTEGER);
        } else {
          statement.setLong(index++, access);
        }
        statement.setString(index++, method.getCanonicalDescription());

        IClass clazz = method.getIClass();
        statement.setLong(index++, clazz.getId());

        IClass returnType = method.getReturn();
        statement.setLong(index++, returnType.getId());

        String signature = method.getCanonicalSignature();
        if (signature == null) {
          statement.setNull(index++, Types.VARCHAR);
        } else {
          statement.setString(index++, signature);
        }
      }

      @Override
      public String getSql() {
        return "INSERT INTO METHODS (META_ID, NAME, ACCESS, DESCRIPTION, CLASS_ID, RETURN_ID, SIGNATURE) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
      }

    }, new ResultRowMapper<Long>() {

      @Override
      public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
      }
    });
    if (method.getParameters() != null) {
      List<IClass> parameters = method.getParameters();
      for (final IClass parameterClazz : parameters) {
        helper.update(new PreparedStatementCreator() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setLong(1, id);
            statement.setLong(2, parameterClazz.getId());
          }

          @Override
          public String getSql() {
            return "INSERT INTO METHOD_PARAMETERS (METHOD_ID, PARAMETER_ID) VALUES (?, ?)";
          }
        });
      }
    }
    if (method.getExceptions() != null) {
      List<IClass> exceptions = method.getExceptions();
      for (final IClass exceptionClazz : exceptions) {
        helper.update(new PreparedStatementCreator() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setLong(1, id);
            statement.setLong(2, exceptionClazz.getId());
          }

          @Override
          public String getSql() {
            return "INSERT INTO METHOD_EXCEPTIONS (METHOD_ID, EXCEPTION_ID) VALUES (?, ?)";
          }
        });
      }
    }

    return getMethodById(id);
  }
}
