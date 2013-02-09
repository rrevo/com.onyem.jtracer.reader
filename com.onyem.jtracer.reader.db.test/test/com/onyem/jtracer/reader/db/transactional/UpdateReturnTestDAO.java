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

public class UpdateReturnTestDAO {

  private IJdbcHelper helper;
  private IntRowMapper intRowMapper = new IntRowMapper();
  private FooRowMapper fooRowMapper = new FooRowMapper();

  @Inject
  UpdateReturnTestDAO(@Assisted IJdbcHelper helper) {
    this.helper = helper;
  }

  void setup() {
    helper
        .execute("CREATE TABLE Foo(Key INT NOT NULL AUTO_INCREMENT, Bar VARCHAR(10), Baz VARCHAR(10), PRIMARY KEY (Key))");
    helper.execute("INSERT INTO Foo (Bar, Baz) VALUES('a', 'A')");
    helper.execute("INSERT INTO Foo (Bar, Baz) VALUES('b', 'B')");
  }

  @Transactional
  List<ConnectionInfo> updateReturnTest() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    int id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    assertData(3, "c", "C", id);
    id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    assertData(4, "d", "D", id);
    infos.addAll(updateReturnTestNested());
    return infos;
  }

  @Transactional
  private List<ConnectionInfo> updateReturnTestNested() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    int id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    assertData(5, "e", "E", id);
    id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    assertData(6, "f", "F", id);
    infos.addAll(updateNestedReturnNonTransactionTest());
    return infos;
  }

  List<ConnectionInfo> updateNestedReturnNonTransactionTest() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    int id = helper.update(new PreparedStatementCreator() {

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
    }, intRowMapper);
    assertData(7, "z", "Z", id);
    return infos;
  }

  List<ConnectionInfo> updateReturnNonTransactionTest() {
    final List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
    int id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    assertData(3, "g", "G", id);
    id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    assertData(4, "h", "H", id);
    return infos;
  }

  @Transactional
  List<ConnectionInfo> updateReturnExceptionalTest() {
    int id = helper.update(new PreparedStatementCreator() {

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
        return "INSERT INTO Foo (Bar, Baz) VALUES(?, ?)";
      }
    }, intRowMapper);
    Assert.assertEquals(3, id);
    throw new RuntimeException("test");
  }

  void assertData(final int key, final String bar, final String baz, int id) {
    Assert.assertEquals(key, id);
    assertData(key, bar, baz);
  }

  void assertData(final int key, final String bar, final String baz) {
    // Tests helper.query
    List<Foo> foos = helper.query(
        "SELECT Key, Bar, Baz FROM Foo WHERE Key = ?", new ParameterSource() {

          @Override
          public void setParameters(PreparedStatement statement)
              throws SQLException {
            statement.setInt(1, key);
          }
        }, fooRowMapper);

    Assert.assertEquals(1, foos.size());

    Foo foo = foos.get(0);
    Assert.assertEquals(key, foo.key);
    Assert.assertEquals(bar, foo.bar);
    Assert.assertEquals(baz, foo.baz);
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

  private static class Foo {
    int key;
    String bar;
    String baz;
  }

  private static class FooRowMapper implements ResultRowMapper<Foo> {

    @Override
    public Foo mapRow(ResultSet rs, int rowNum) throws SQLException {
      Foo foo = new Foo();
      foo.key = rs.getInt("Key");
      foo.bar = rs.getString("Bar");
      foo.baz = rs.getString("Baz");
      return foo;
    }
  }

  private static class IntRowMapper implements ResultRowMapper<Integer> {

    @Override
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
      return rs.getInt(1);
    }
  }
}
