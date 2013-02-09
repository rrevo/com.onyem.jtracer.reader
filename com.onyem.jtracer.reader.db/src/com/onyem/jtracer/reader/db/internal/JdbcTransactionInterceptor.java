package com.onyem.jtracer.reader.db.internal;

import java.sql.Connection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.onyem.jtracer.reader.db.Transactional;

/**
 * Implementation of the {@link Transactional} annotation
 */
public class JdbcTransactionInterceptor implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    boolean wasInTransaction = ConnectionHolder.isTransaction();
    if (!wasInTransaction) {
      ConnectionHolder.beginTransaction();
    }
    Exception exceptionThrown = null;
    try {
      return methodInvocation.proceed();
    } catch (Exception exception) {
      exceptionThrown = exception;
      throw exception;
    } finally {
      if (!wasInTransaction) {
        Connection connection = ConnectionHolder.getConnection();
        if (connection != null) {
          try {
            if (exceptionThrown == null) {
              connection.commit();
            } else {
              connection.rollback();
            }
          } finally {
            ConnectionHolder.clearConnection();
          }
        }
      }
    }
  }

}
