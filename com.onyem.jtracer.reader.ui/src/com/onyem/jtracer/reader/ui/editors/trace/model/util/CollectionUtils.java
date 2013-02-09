package com.onyem.jtracer.reader.ui.editors.trace.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class CollectionUtils {

  private CollectionUtils() {
  }

  public static <E> List<E> unmodifiableCopy(List<E> list) {
    List<E> listClone = new ArrayList<E>();
    listClone.addAll(list);
    return Collections.unmodifiableList(listClone);
  }

  public static <E> Set<E> unmodifiableCopy(Set<E> set) {
    Set<E> setClone = new LinkedHashSet<E>();
    setClone.addAll(set);
    return Collections.unmodifiableSet(setClone);
  }
}
