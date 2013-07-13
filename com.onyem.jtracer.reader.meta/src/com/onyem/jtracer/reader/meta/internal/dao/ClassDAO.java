package com.onyem.jtracer.reader.meta.internal.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.onyem.jtracer.reader.db.util.StringParameterSource;
import com.onyem.jtracer.reader.meta.Activator;
import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.ClassType;
import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.internal.ClassNameUtils;
import com.onyem.jtracer.reader.meta.internal.IMetaServiceExtended;
import com.onyem.jtracer.reader.meta.internal.Messages;

@DAO
@Immutable
public class ClassDAO {

  private final IMetaServiceExtended metaService;
  private final ClassNameUtils nameUtils;
  private final IJdbcHelper helper;

  @Inject
  public ClassDAO(ClassNameUtils nameUtils, IJdbcHelperFactory helperFactory,
      @Assisted IMetaServiceExtended metaService,
      @Assisted IConnectionManager connectionManager) {
    this.metaService = metaService;
    this.nameUtils = nameUtils;
    helper = helperFactory.create(connectionManager);
  }

  @Transactional
  public IClass getClassByMetaId(final long metaId) {
    ParameterSource parameterSource = new LongParameterSource(metaId);
    final String whereClause = "C.META_ID = ?";
    return getClassWithInterfaces(parameterSource, whereClause);
  }

  @Transactional
  public IClass getClassById(final long id) {
    ParameterSource parameterSource = new LongParameterSource(id);
    String whereClause = "C.ID = ?";
    return getClassWithInterfaces(parameterSource, whereClause);
  }

  @Transactional
  public ClassId getClassIdById(final long id) {
    ParameterSource parameterSource = new LongParameterSource(id);
    String whereClause = "C.ID = ?";
    return getClassWithoutInterfaces(parameterSource, whereClause).getId();
  }

  @Transactional
  public IClass getClassByName(final String packageName,
      final String className, final boolean isInterface) {
    ParameterSource parameterSource = null;
    String whereClause = null;
    if (packageName == null) {
      parameterSource = new ParameterSource() {

        @Override
        public void setParameters(PreparedStatement statement)
            throws SQLException {
          statement.setString(1, className);
          statement.setBoolean(2, isInterface);
        }
      };
      whereClause = "C.NAME = ? AND C.PACKAGE IS NULL AND C.IS_INTERFACE = ?";
    } else {
      parameterSource = new ParameterSource() {

        @Override
        public void setParameters(PreparedStatement statement)
            throws SQLException {
          statement.setString(1, className);
          statement.setString(2, packageName);
          statement.setBoolean(3, isInterface);
        }
      };
      whereClause = "C.NAME = ? AND C.PACKAGE = ? AND C.IS_INTERFACE = ?";
    }
    return getClassWithInterfaces(parameterSource, whereClause);
  }

  @Transactional
  public IClass getPrimitiveByName(final String name) {
    ParameterSource parameterSource = new StringParameterSource(name);
    String whereClause = "C.NAME = ? AND C.IS_PRIMITIVE = 1";
    return getClassWithoutInterfaces(parameterSource, whereClause);
  }

  @Transactional
  public IClass getArrayByComponentType(final IClass componentType) {
    ParameterSource parameterSource = new LongParameterSource(componentType
        .getId().getId());
    String whereClause = "C.COMPONENT_ID = ? AND C.IS_ARRAY = 1";
    return getClassWithoutInterfaces(parameterSource, whereClause);
  }

  private IClass getClassWithInterfaces(ParameterSource parameterSource,
      String whereClause) {
    List<ClassId> interfaces = helper.query(getInterfaceQuery() + whereClause,
        parameterSource, new ClassIdResultRowMapper());
    Set<ClassId> interfacesSet = toSet(interfaces);

    ClassResultRowMapper classResultRowMapper = new ClassResultRowMapper(
        metaService, nameUtils);
    classResultRowMapper.setInterfaces(interfacesSet);

    IClass clazz = helper.queryForObject("SELECT * FROM CLASSES C WHERE "
        + whereClause, parameterSource, classResultRowMapper);

    return clazz;
  }

  private String getInterfaceQuery() {
    return "SELECT I.ID "
        + " FROM CLASSES I JOIN CLASS_INTERFACES CI ON I.ID = CI.INTERFACE_ID "
        + " JOIN CLASSES C ON CI.CLASS_ID = C.ID WHERE ";
  }

  private <E> Set<E> toSet(List<E> list) {
    Set<E> set = new HashSet<E>();
    set.addAll(list);
    return set;
  }

  private IClass getClassWithoutInterfaces(ParameterSource parameterSource,
      String whereClause) {
    ClassResultRowMapper classResultRowMapper = new ClassResultRowMapper(
        metaService, nameUtils);
    IClass clazz = helper.queryForObject("SELECT * FROM CLASSES C WHERE "
        + whereClause, parameterSource, classResultRowMapper);
    return clazz;
  }

  @Transactional
  public IClass insertClass(final IClass clazz) {

    // If this class exists then merge the new data
    if (clazz.getClassType() == ClassType.CLASS) {
      IClass existingClass = getClassByName(clazz.getPackageName(),
          clazz.getClassName(), false);
      if (existingClass != null) {
        return mergeClass(clazz, existingClass);
      }
    }

    final long id = helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        Long metaId = clazz.getMetaId();
        int index = 1;
        if (metaId == null) {
          statement.setNull(index++, Types.BIGINT);
        } else {
          statement.setLong(index++, metaId);
        }
        Integer access = clazz.getAccess();
        if (access == null) {
          statement.setNull(index++, Types.INTEGER);
        } else {
          statement.setInt(index++, access);
        }
        ClassType classType = clazz.getClassType();
        statement.setBoolean(index++, classType == ClassType.CLASS);
        statement.setBoolean(index++, classType == ClassType.INTERFACE);
        statement.setBoolean(index++, classType == ClassType.ARRAY);

        //New primitives cannot be inserted
        statement.setBoolean(index++, false);

        if (clazz.getClassName() == null) {
          statement.setNull(index++, Types.VARCHAR);
        } else {
          statement.setString(index++, clazz.getClassName());
        }

        if (clazz.getPackageName() == null) {
          statement.setNull(index++, Types.VARCHAR);
        } else {
          statement.setString(index++, clazz.getPackageName());
        }

        if (clazz.getCanonicalSignature() == null) {
          statement.setNull(index++, Types.VARCHAR);
        } else {
          statement.setString(index++, clazz.getCanonicalSignature());
        }

        ClassId superClass = clazz.getSuperClass();
        if (superClass == null) {
          statement.setNull(index++, Types.BIGINT);
        } else {
          statement.setLong(index++, superClass.getId());
        }
        ClassId componentType = clazz.getComponentType();
        if (componentType == null) {
          statement.setNull(index++, Types.BIGINT);
        } else {
          statement.setLong(index++, componentType.getId());
        }
      }

      @Override
      public String getSql() {
        return "INSERT INTO CLASSES (META_ID, ACCESS, IS_CLASS, IS_INTERFACE, IS_ARRAY, IS_PRIMITIVE, "
            + " NAME, PACKAGE, SIGNATURE, SUPERCLASS_ID, COMPONENT_ID) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      }

    }, new ResultRowMapper<Long>() {

      @Override
      public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
      }
    });
    if (clazz.getInterfaces() != null) {
      Set<ClassId> interfaces = clazz.getInterfaces();
      for (final ClassId interfaceClazz : interfaces) {
        helper.update(new PreparedStatementCreator() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setLong(1, id);
            statement.setLong(2, interfaceClazz.getId());
          }

          @Override
          public String getSql() {
            return "INSERT INTO CLASS_INTERFACES (CLASS_ID, INTERFACE_ID) VALUES (?, ?)";
          }
        });
      }
    }
    return getClassById(id);
  }

  /*
   * Merge a class where only the name is present (loaded as part of an
   * exception trace) to one where all additional fields are known.
   * 
   * See https://github.com/rrevo/com.onyem.jtracer.reader/issues/2
   */
  @Transactional
  private IClass mergeClass(final IClass clazz, final IClass existingClass) {
    if (existingClass.getClassType() != ClassType.CLASS) {
      throw new UnsupportedOperationException();
    }

    Activator.logInfo(Messages.MERGE_CLASS + " " + clazz + " " + existingClass,
        null);

    assert existingClass.getMetaId() == null;
    assert existingClass.getAccess() == null;
    assert existingClass.getClassType() == ClassType.CLASS;
    assert existingClass.getCanonicalName().equals(clazz.getCanonicalName());
    assert existingClass.getSuperClass() == null;
    assert existingClass.getInterfaces().isEmpty();

    final long id = existingClass.getId().getId();

    final Object ret = helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        Long metaId = clazz.getMetaId();
        int index = 1;
        if (metaId == null) {
          statement.setNull(index++, Types.BIGINT);
        } else {
          statement.setLong(index++, metaId);
        }
        Integer access = clazz.getAccess();
        if (access == null) {
          statement.setNull(index++, Types.INTEGER);
        } else {
          statement.setInt(index++, access);
        }

        if (clazz.getCanonicalSignature() == null) {
          statement.setNull(index++, Types.VARCHAR);
        } else {
          statement.setString(index++, clazz.getCanonicalSignature());
        }

        ClassId superClass = clazz.getSuperClass();
        if (superClass == null) {
          statement.setNull(index++, Types.BIGINT);
        } else {
          statement.setLong(index++, superClass.getId());
        }

        statement.setLong(index++, id);
      }

      @Override
      public String getSql() {
        return "UPDATE CLASSES SET META_ID = ? , ACCESS = ? , "
            + " SIGNATURE = ? , SUPERCLASS_ID = ? WHERE ID = ?";
      }

    }, new ResultRowMapper<Long>() {

      @Override
      public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
      }
    });
    if (clazz.getInterfaces() != null) {
      Set<ClassId> interfaces = clazz.getInterfaces();
      for (final ClassId interfaceClazz : interfaces) {
        helper.update(new PreparedStatementCreator() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setLong(1, id);
            statement.setLong(2, interfaceClazz.getId());
          }

          @Override
          public String getSql() {
            return "INSERT INTO CLASS_INTERFACES (CLASS_ID, INTERFACE_ID) VALUES (?, ?)";
          }
        });
      }
    }

    assert ret == null;
    return getClassById(id);
  }
}
