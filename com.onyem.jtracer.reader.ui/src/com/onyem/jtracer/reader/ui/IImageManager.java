package com.onyem.jtracer.reader.ui;

import org.eclipse.swt.graphics.Image;

public interface IImageManager {

  public static final String METHOD_ENTRY = "method_enter";
  public static final String METHOD_EXIT = "method_exit";
  public static final String EXCEPTION_CATCH = "exception_catch";
  public static final String EXCEPTION_THROW = "exception_throw";

  public static final String WARNING = "warning";
  public static final String LOGO = "logo";

  public static final int IMAGE_SIZE = 16;

  public Image getImage(String name);
}
