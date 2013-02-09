package com.onyem.jtracer.reader.ui.editors;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class FileEditorInput implements IEditorInput {

  private final String folderName;
  private final String path;

  public FileEditorInput(File file) {
    this.path = file.getPath();
    folderName = file.getParentFile().getName();
  }

  @SuppressWarnings("rawtypes")
  public Object getAdapter(Class adapter) {
    return null;
  }

  public boolean exists() {
    return true;
  }

  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  public String getName() {
    return folderName;
  }

  public IPersistableElement getPersistable() {
    return null;
  }

  public String getToolTipText() {
    return folderName;
  }

  public String getPath() {
    return path;
  }

}
