package com.onyem.jtracer.reader.meta;

import java.util.Set;

import javax.annotation.Nullable;

public interface IClass {

  long getId();

  @Nullable
  Long getMetaId();

  //  Flag Name      Value   Interpretation
  //  ACC_PUBLIC     0x0001  Declared public; may be accessed from outside its package.
  //  ACC_FINAL      0x0010  Declared final; no subclasses allowed.
  //  ACC_SUPER      0x0020  Treat superclass methods specially when invoked by the invokespecial instruction.
  //  ACC_INTERFACE  0x0200  Is an interface, not a class.
  //  ACC_ABSTRACT   0x0400  Declared abstract; may not be instantiated.
  //  ACC_SYNTHETIC  0x1000  Declared synthetic; not present in the source code.
  //  ACC_ANNOTATION 0x2000  Declared as an annotation type.
  //  ACC_ENUM       0x4000  Declared as an enum type.
  @Nullable
  Integer getAccess();

  ClassType getClassType();

  @Nullable
  String getPackageName();

  @Nullable
  String getClassName();

  @Nullable
  IClass getSuperClass();

  @Nullable
  Set<IClass> getInterfaces();

  @Nullable
  IClass getComponentType();

  @Nullable
  String getCanonicalSignature();

  String getSimpleName();

  String getCompleteName();

  String getCanonicalName();
}
