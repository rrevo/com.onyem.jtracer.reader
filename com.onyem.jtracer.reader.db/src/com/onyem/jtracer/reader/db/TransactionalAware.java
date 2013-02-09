package com.onyem.jtracer.reader.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for methods that support Jdbc transactions as detailed in
 * {@link Transactional}
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TransactionalAware {

}
