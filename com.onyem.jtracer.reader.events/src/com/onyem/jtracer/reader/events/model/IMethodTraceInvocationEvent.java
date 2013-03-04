package com.onyem.jtracer.reader.events.model;

import java.util.List;

import com.onyem.jtracer.reader.meta.IMethod;

public interface IMethodTraceInvocationEvent extends IInvocationEvent {

  List<IMethod> getMethodTrace();

}
