package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import com.onyem.jtracer.reader.meta.IClass;

/**
 * Specification for the class selector, parameter - selector:
 * A class is included if atleast one rule causes Inclusion and no rule causes
 * Exclusion. Ordering of rules does not matter.
 * 
 * By default nothing is done
 * + for an include rule
 * - for an exclude rule
 * Multiple rules are separated by ,
 * Support *, **
 * 
 * Examples
 * -foo/bar/Baz -> Exclude foo.bar.Baz
 * +foo/bar/* -> Include classes in package foo.bar
 * +foo/bar/** -> Include classes in package foo.bar and all sub packages
 * +* -> Include classes in the default package
 * +** -> Include all classes
 * -** -> Exclude everything since exclude has higher precedence than include
 * 
 */
public interface IClassTraceChecker {

  public boolean isTraced(IClass clazz);

  public boolean isTraced(String className);

}
