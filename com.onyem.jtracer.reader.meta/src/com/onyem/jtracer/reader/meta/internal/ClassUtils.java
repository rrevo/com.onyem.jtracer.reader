package com.onyem.jtracer.reader.meta.internal;

import java.util.ArrayList;
import java.util.List;

import com.onyem.jtracer.reader.meta.ClassId;
import com.onyem.jtracer.reader.meta.IClass;

public class ClassUtils {

  public static List<ClassId> getClassIds(List<IClass> classes) {
    List<ClassId> classIds = new ArrayList<ClassId>(classes.size());
    for (IClass clazz : classes) {
      classIds.add(clazz.getId());
    }
    return classIds;
  }

}
