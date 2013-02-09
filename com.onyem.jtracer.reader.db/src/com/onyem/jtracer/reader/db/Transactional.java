package com.onyem.jtracer.reader.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Jdbc calls within {@link TransactionalAware} methods are implemented for
 * Transaction support.
 * 
 * All nested Jdbc calls will be part of the same transaction and will be
 * commit'ed if there is no exception. In case of an exception the rollback
 * occurs. All the Jdbc calls should be in the same thread.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transactional {

}
