package com.onyem.jtracer.reader.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.onyem.jtracer.reader.ui.actions.AboutAction;
import com.onyem.jtracer.reader.ui.actions.OnlineHelpAction;
import com.onyem.jtracer.reader.ui.actions.OpenFileAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

  private OpenFileAction openAction;
  private IWorkbenchAction exitAction;

  private IWorkbenchAction introAction;
  private OnlineHelpAction onlineHelpAction;
  private AboutAction aboutAction;

  public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
  }

  protected void makeActions(IWorkbenchWindow window) {
    openAction = new OpenFileAction(window);
    register(openAction);
    exitAction = ActionFactory.QUIT.create(window);
    register(exitAction);

    introAction = ActionFactory.INTRO.create(window);
    register(introAction);
    onlineHelpAction = new OnlineHelpAction(window);
    register(onlineHelpAction);
    aboutAction = new AboutAction(window);
  }

  protected void fillMenuBar(IMenuManager menuBar) {
    MenuManager fileMenu = new MenuManager("&File", null);
    fileMenu.add(openAction);
    //    fileMenu.add(agentConfigurationAction);
    fileMenu.add(new Separator());
    fileMenu.add(exitAction);
    menuBar.add(fileMenu);

    MenuManager helpMenu = new MenuManager("&Help", null);
    helpMenu.add(introAction);
    helpMenu.add(new Separator());
    helpMenu.add(onlineHelpAction);
    helpMenu.add(aboutAction);
    menuBar.add(helpMenu);
  }

}
