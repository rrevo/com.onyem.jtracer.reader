package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.meta.ClassType;
import com.onyem.jtracer.reader.meta.IClass;

@Immutable
public class ClassNameRule {

  public enum Effect {
    NO_EFFECT, INCLUDE, EXCLUDE
  }

  final private boolean include;
  final private String regex;
  final private String regexDisplay;
  final private ClassNameMatcher classNameMatcher;

  public static ClassNameRule createRule(String rule) {
    String operation = rule.substring(0, 1);
    String regex = rule.substring(1);
    if (operation.equalsIgnoreCase("+")) {
      return new ClassNameRule(true, regex);
    } else {
      return new ClassNameRule(false, regex);
    }
  }

  public static ClassNameRule createExcludeRule(IClass invocationClass) {
    if (!(invocationClass.getClassType() == ClassType.CLASS || invocationClass
        .getClassType() == ClassType.INTERFACE)) {
      throw new IllegalArgumentException();
    }
    String className = invocationClass.getCompleteName();
    return new ClassNameRule(false, className);
  }

  protected ClassNameRule(boolean include, String regex) {
    this.include = include;
    this.regex = regex.replace(".", "/");
    this.regexDisplay = regex.replace("/", ".");
    this.classNameMatcher = new ClassNameMatcher();
  }

  public boolean isInclude() {
    return include;
  }

  public String getRegex() {
    return regex;
  }

  public String getDisplayRegex() {
    return regexDisplay;
  }

  public Effect checkClass(String className) {
    // Agent and Asm classes are always excluded
    if (className.startsWith("com/onyem/agent/collector/")) {
      return Effect.EXCLUDE;
    }
    if (className.startsWith("org/objectweb/asm/")) {
      return Effect.EXCLUDE;
    }
    if (classNameMatcher.isMatch(className, regex)) {
      if (include) {
        return Effect.INCLUDE;
      } else {
        return Effect.EXCLUDE;
      }
    }
    return Effect.NO_EFFECT;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (include ? 1231 : 1237);
    result = prime * result + ((regex == null) ? 0 : regex.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ClassNameRule other = (ClassNameRule) obj;
    if (include != other.include)
      return false;
    if (regex == null) {
      if (other.regex != null)
        return false;
    } else if (!regex.equals(other.regex))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ClassNameRule [include=" + include + ", regex=" + regex + "]";
  }

}
