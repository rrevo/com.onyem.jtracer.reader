package com.onyem.jtracer.reader.db.transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ParameterSource;
import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.db.Transactional;
import com.onyem.jtracer.reader.db.internal.NullParameterSource;

public class ExecuteTestDAO {

  private IJdbcHelper helper;

  @Inject
  ExecuteTestDAO(@Assisted IJdbcHelper helper) {
    this.helper = helper;
  }

  void setup() {
    helper.execute("CREATE TABLE Foo( Bar VARCHAR(10), Baz VARCHAR(10))");
    helper.execute("INSERT INTO Foo VALUES('a', 'A')");
    helper.execute("INSERT INTO Foo VALUES('b', 'B')");
  }

  @Transactional
  void executeTest() {
    TestHelper.assertInTransaction();
    helper.execute("INSERT INTO Foo VALUES('c', 'C')");
    assertData("c", "C");
    ConnectionInfo info = TestHelper.getConnectionInfo();
    helper.execute("INSERT INTO Foo VALUES('d', 'D')");
    assertData("d", "D");
    TestHelper.assertInTransaction(info);
    executeTestNested(info);
  }

  @Transactional
  private void executeTestNested(ConnectionInfo info) {
    helper.execute("INSERT INTO Foo VALUES('e', 'E')");
    assertData("e", "E");
    TestHelper.assertInTransaction(info);
    helper.execute("INSERT INTO Foo VALUES('f', 'F')");
    assertData("f", "F");
    TestHelper.assertInTransaction(info);
    executeNestedNonTransactionTest(info);
  }

  private void executeNestedNonTransactionTest(ConnectionInfo info) {
    helper.execute("INSERT INTO Foo VALUES('z', 'Z')");
    assertData("z", "Z");
    TestHelper.assertInTransaction(info);
  }

  void executeNonTransactionTest() {
    TestHelper.assertNotInTransaction();
    helper.execute("INSERT INTO Foo VALUES('g', 'G')");
    assertData("g", "G");
    TestHelper.assertNotInTransaction();
    helper.execute("INSERT INTO Foo VALUES('h', 'H')");
    assertData("h", "H");
    TestHelper.assertNotInTransaction();
  }

  @Transactional
  List<ConnectionInfo> executeExceptionalTest() {
    TestHelper.assertInTransaction();
    helper.execute("INSERT INTO Foo VALUES('i', 'I')");
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