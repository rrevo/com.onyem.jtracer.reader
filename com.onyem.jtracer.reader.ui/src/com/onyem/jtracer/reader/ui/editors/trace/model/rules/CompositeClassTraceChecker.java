package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.ui.editors.trace.model.util.CollectionUtils;

/*
 * Rules from multiple IClassTraceChecker's are combined into one. 
 * this will act differently compared to the individual IClassTraceChecker's
 * combined as the rules are not always commutative   
 */
@Immutable
class CompositeClassTraceChecker extends AbstractRulesClassTraceChecker {

  final private Set<ClassNameRule> rules;

  public CompositeClassTraceChecker(Set<IRuleClassTraceChecker> traceCheckers) {
    Set<ClassNameRule> localRules = new HashSet<ClassNameRule>();
    for (IRuleClassTraceChecker traceChecker : traceCheckers) {
      localRules.addAll(traceChecker.getRules());
    }
    rules = CollectionUtils.unmodifiableCopy(localRules);
  }

  @Override
  public final Set<ClassNameRule> getRules() {
    return rules;
  }

  @Override
  public String toString() {
    return "CompositeClassTraceChecker [rules=" + rules + "]";
  }

}
