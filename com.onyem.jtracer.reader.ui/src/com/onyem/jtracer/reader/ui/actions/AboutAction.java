package com.onyem.jtracer.reader.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.util.Constants;

public class AboutAction extends Action {

  public static final String ID = "com.onyem.jtracer.reader.ui.actions.AboutAction";

  private final IWorkbenchWindow workbenchWindow;

  public AboutAction(IWorkbenchWindow workbenchWindow) {
    this.workbenchWindow = workbenchWindow;
    setId(ID);
    setActionDefinitionId(ID);
    setText("&About");
  }

  @Override
  public void run() {
    if (workbenchWindow != null) {
      Shell shell = workbenchWindow.getShell();
      TextDialog aboutDialog = new TextDialog(Activator.getQueue(), shell,
          "About", Constants.APPLICATION_NAME + " by "
              + Constants.PROVIDER_NAME);
      aboutDialog.open();
    }
  }
}
