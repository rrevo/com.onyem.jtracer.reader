package com.onyem.jtracer.reader.ui.util;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class SWTResourceManager {

  private static final FontRegistry FONT_REGISTRY = JFaceResources
      .getFontRegistry();

  public static Font getBoldFont() {
    return FONT_REGISTRY.getBold(JFaceResources.DEFAULT_FONT);
  }

  public static Color getWhiteColor() {
    return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
  }

}