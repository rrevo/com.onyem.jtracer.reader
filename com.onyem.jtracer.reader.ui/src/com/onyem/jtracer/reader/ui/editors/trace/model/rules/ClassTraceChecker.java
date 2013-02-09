package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.ui.editors.trace.model.util.CollectionUtils;

@Immutable
class ClassTraceChecker extends AbstractRulesClassTraceChecker {

  final private Set<ClassNameRule> rules;

  public ClassTraceChecker(Set<ClassNameRule> rules) {
    this.rules = CollectionUtils.unmodifiableCopy(rules);
  }

  @Override
  public final Set<ClassNameRule> getRules() {
    return rules;
  }

  @Override
  public String toString() {
    return "ClassTraceChecker [rules=" + rules + "]";
  }

}
