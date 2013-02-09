package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ClassTraceCheckerFactory {

  public static IRuleClassTraceChecker createClassTraceChecker(
      Set<ClassNameRule> rules) {
    return new ClassTraceChecker(rules);
  }
}
