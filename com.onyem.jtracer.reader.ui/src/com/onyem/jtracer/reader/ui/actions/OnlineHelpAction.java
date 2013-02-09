package com.onyem.jtracer.reader.ui.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.dialogs.ErrorDialog;
import com.onyem.jtracer.reader.ui.util.Constants;
import com.onyem.jtracer.reader.ui.util.Messages;

public class OnlineHelpAction extends Action {

  public static final String ID = "com.onyem.jtracer.reader.ui.actions.OnlineHelpAction";

  private final IWorkbenchWindow window;

  public OnlineHelpAction(IWorkbenchWindow window) {
    this.window = window;
    setId(ID);
    setActionDefinitionId(ID);
    setText("Online &Help");
  }

  @Override
  public void run() {
    try {
      PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
          .openURL(new URL(Constants.URL_HELP));
    } catch (PartInitException e) {
      String message = Messages.ERROR_OPENING_HELP;
      Activator.logError(message, e);
      ErrorDialog dialog = new ErrorDialog(window.getShell(), message, e);
      dialog.open();
    } catch (MalformedURLException e) {
      String message = Messages.ERROR_OPENING_HELP;
      Activator.logError(message, e);
      ErrorDialog dialog = new ErrorDialog(window.getShell(), message, e);
      dialog.open();
    }
  }
}
