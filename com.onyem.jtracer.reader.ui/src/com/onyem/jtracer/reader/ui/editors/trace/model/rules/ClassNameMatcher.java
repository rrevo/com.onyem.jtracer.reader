package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import javax.annotation.concurrent.Immutable;

@Immutable
class ClassNameMatcher {

  boolean isMatch(String className, String regex) {
    // All wildcard
    if (regex.equals("**")) {
      return true;
    }
    // Specific match
    if (regex.equals(className)) {
      return true;
    }
    // All in subpackage
    if (regex.endsWith("**")) {
      String packageName = regex.substring(0, regex.length() - 2);
      if (className.startsWith(packageName)) {
        return true;
      }
    }
    // All in package
    if (regex.endsWith("*")) {
      String packageName = regex.substring(0, regex.length() - 1);
      // classname starts with the package
      if (className.indexOf(packageName) == 0) {
        // no other / exists
        if (className.indexOf("/", packageName.length()) == -1) {
          return true;
        }
      }
    }
    return false;
  }
}
