package com.onyem.jtracer.reader.ui;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.onyem.jtracer.reader.ui.editors.FileEditorInput;
import com.onyem.jtracer.reader.ui.editors.TraceEditor;
import com.onyem.jtracer.reader.ui.util.Messages;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    super(configurer);
  }

  public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
    return new ApplicationActionBarAdvisor(configurer);
  }

  @Override
  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setShowCoolBar(false);
    configurer.setShowStatusLine(false);
    configurer.setShowMenuBar(true);
    configurer.setTitle("Reader - Onyem");
  }

  @Override
  public void postWindowOpen() {
    String[] applicationArgs = Platform.getApplicationArgs();
    if (applicationArgs.length == 1) {
      File file = new File(applicationArgs[0]);
      IEditorInput editorInput = new FileEditorInput(file);
      IWorkbenchWindow window = PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow();
      IWorkbenchPage page = window.getActivePage();
      try {
        page.openEditor(editorInput, TraceEditor.ID);
      } catch (PartInitException e) {
        Activator.logError(Messages.ERROR_OPENING_TRACE, e);
      }
    }
  }
}
