package com.onyem.jtracer.reader.events.model;

import com.onyem.jtracer.reader.meta.IMethod;

public interface IMethodInvocationEvent extends IInvocationEvent {

  IMethod getMethod();

}
