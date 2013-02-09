package com.onyem.jtracer.reader.ui.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.onyem.jtracer.reader.ui.editors.FileEditorInput;
import com.onyem.jtracer.reader.ui.editors.TraceEditor;
import com.onyem.jtracer.reader.ui.util.Constants;

public class OpenFileAction extends Action {

  public static final String ID = "com.onyem.jtracer.reader.ui.actions.OpenFileAction";

  private final IWorkbenchWindow window;

  public OpenFileAction(IWorkbenchWindow window) {
    this.window = window;
    setId(ID);
    setActionDefinitionId(ID);
    setText("&Open JTrace");
  }

  @Override
  public void run() {
    FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN);
    fileDialog.setFilterExtensions(new String[] { Constants.TRACE_FILE_NAME });
    String selectedFile = fileDialog.open();
    if (selectedFile != null) {
      IWorkbenchPage activePage = window.getActivePage();
      IEditorInput input = new FileEditorInput(new File(selectedFile));
      try {
        activePage.openEditor(input, TraceEditor.ID);
      } catch (PartInitException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
