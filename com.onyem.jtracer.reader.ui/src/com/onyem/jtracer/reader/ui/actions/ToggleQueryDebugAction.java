package com.onyem.jtracer.reader.ui.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.service.prefs.BackingStoreException;

import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.dialogs.ErrorDialog;
import com.onyem.jtracer.reader.ui.editors.TraceEditor;
import com.onyem.jtracer.reader.ui.util.Constants;
import com.onyem.jtracer.reader.ui.util.Messages;

public class ToggleQueryDebugAction extends Action {

  public static final String ID = "com.onyem.jtracer.reader.ui.actions.ToggleQueryDebugAction";

  private final IWorkbenchWindow window;

  public ToggleQueryDebugAction(IWorkbenchWindow window) {
    this.window = window;
    setId(ID);
    setActionDefinitionId(ID);
    setText("&Toggle Query Debug");
  }

  @Override
  public void run() {
    IPreferencesService preferencesService = Platform.getPreferencesService();
    toggleQueryDebugPref(!preferencesService.getBoolean(TraceEditor.ID,
        Constants.PREF_QUERY_DEBUG, false, null));
  }

  void toggleQueryDebugPref(boolean newValue) {
    IEclipsePreferences node = ConfigurationScope.INSTANCE
        .getNode(TraceEditor.ID);
    node.putBoolean(Constants.PREF_QUERY_DEBUG, newValue);
    // Forces the application to save the preferences
    try {
      node.flush();
    } catch (BackingStoreException e) {
      Activator.logError(Messages.ERROR_SAVING_PREFS, e);
      ErrorDialog dialog = new ErrorDialog(window.getShell(),
          Messages.ERROR_SAVING_PREFS, e);
      dialog.open();
    }

  }
}
