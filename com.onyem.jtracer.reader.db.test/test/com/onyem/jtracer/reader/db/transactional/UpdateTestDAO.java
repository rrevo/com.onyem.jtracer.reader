package com.onyem.jtracer.reader.db.transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.PreparedStatementCreator;
import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.db.Transactional;
import com.onyem.jtracer.reader.db.internal.NullParameterSource;

public class UpdateTestDAO {

  private IJdbcHelper helper;

  @Inject
  UpdateTestDAO(@Assisted IJdbcHelper helper) {
    this.helper = helper;
  }

  void setup() {
    helper.execute("CREATE TABLE Foo( Bar VARCHAR(10), Baz VARCHAR(10))");
    helper.execute("INSERT INTO Foo VALUES('a', 'A')");
    helper.execute("INSERT INTO Foo VALUES('b', 'B')");
  }

  @Transactional
  List<ConnectionInfo> updateTest() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "c");
        statement.setString(2, "C");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("c", "C");
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "d");
        statement.setString(2, "D");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("d", "D");
    infos.addAll(updateReturnTestNested());
    return infos;
  }

  @Transactional
  private List<ConnectionInfo> updateReturnTestNested() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "e");
        statement.setString(2, "E");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("e", "E");
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "f");
        statement.setString(2, "F");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("f", "F");
    infos.addAll(updateNestedReturnNonTransactionTest());
    return infos;
  }

  List<ConnectionInfo> updateNestedReturnNonTransactionTest() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "z");
        statement.setString(2, "Z");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    });
    assertData("z", "Z");
    return infos;
  }

  List<ConnectionInfo> updateNonTransactionTest() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "g");
        statement.setString(2, "G");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("g", "G");
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        infos.add(info);
        statement.setString(1, "h");
        statement.setString(2, "H");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("h", "H");
    return infos;
  }

  @Transactional
  List<ConnectionInfo> updateExceptionalTest() {
    helper.update(new PreparedStatementCreator() {

      @Override
      public void setParameters(PreparedStatement statement)
          throws SQLException {
        ConnectionInfo info = new ConnectionInfo();
        info.setConnectionData(statement.getConnection());
        statement.setString(1, "i");
        statement.setString(2, "I");
      }

      @Override
      public String getSql() {
        return "INSERT INTO Foo VALUES(?, ?)";
      }
    });
    assertData("i", "I");
    throw new RuntimeException("test");
  }

  // Tests queryForObject
  void assertData(final String bar, final String baz) {
    helper.queryForObject("SELECT Bar, Baz FROM Foo WHERE Bar = ? AND Baz = ?",
        new ParameterSource() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setString(1, bar);
            statement.setString(2, baz);
          }
        }, new ResultRowMapper<Void>() {

          @Override
          public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
            Assert.assertEquals(bar, rs.getString(1));
            Assert.assertEquals(baz, rs.getString(2));
            return null;
          }
        });
  }

  void assertDataCount(final int count) {
    helper.queryForObject("SELECT COUNT(*) FROM Foo ",
        new NullParameterSource(), new ResultRowMapper<Void>() {

          @Override
          public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
            Assert.assertEquals(count, rs.getInt(1));
            return null;
          }
        });
  }
}