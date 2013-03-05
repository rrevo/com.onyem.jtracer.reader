package com.onyem.jtracer.reader.ui.editors.trace.model.rules;

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.onyem.jtracer.reader.meta.IMethod;

@Immutable
public class ClassTraceCheckerFactory {

  public static IRuleClassTraceChecker createClassTraceChecker(
      Set<ClassNameRule> rules) {
    return new ClassTraceChecker(rules);
  }

  public static int getTraceDifference(IClassTraceChecker classTraceChecker,
      List<IMethod> throwMethodStack, List<IMethod> catchMethodStack) {

    int tracedClasssesCount = 0;
    for (int i = 0; i < throwMethodStack.size() - catchMethodStack.size(); i++) {
      IMethod invocationMethod = throwMethodStack.get(i);
      if (classTraceChecker.isTraced(invocationMethod.getIClass()
          .getCompleteName())) {
        tracedClasssesCount++;
      }
    }
    return tracedClasssesCount;
  }
}
