package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.ClassNameRule.Effect;

abstract class AbstractRulesClassTraceChecker implements IRuleClassTraceChecker {

  @Override
  public final boolean isTraced(IClass clazz) {
    return isTraced(clazz.getCompleteName());
  }

  @Override
  public final boolean isTraced(String className) {
    // Atleast one rule should cause Inclusion and no Excludes
    boolean includedOnce = false;
    String convertedClassName = className.replace(".", "/");
    for (ClassNameRule rule : getRules()) {
      Effect effect = rule.checkClass(convertedClassName);
      if (effect == ClassNameRule.Effect.EXCLUDE) {
        return false;
      }
      if (effect == ClassNameRule.Effect.INCLUDE) {
        includedOnce = true;
      }
    }
    return includedOnce;
  }

  /*
   * hashcode is a function of the rules
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public final int hashCode() {
    return getRules().hashCode();
  }

  /*
   * Equality is a function of the rules and nothing else
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AbstractRulesClassTraceChecker)) {
      return false;
    }
    AbstractRulesClassTraceChecker other = (AbstractRulesClassTraceChecker) obj;
    return getRules().equals(other.getRules());
  }

}
