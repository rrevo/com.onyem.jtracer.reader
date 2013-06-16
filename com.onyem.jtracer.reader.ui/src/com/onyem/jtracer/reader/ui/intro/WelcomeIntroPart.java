package com.onyem.jtracer.reader.ui.intro;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.IntroPart;

import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.dialogs.ErrorDialog;
import com.onyem.jtracer.reader.ui.editors.FileEditorInput;
import com.onyem.jtracer.reader.ui.editors.TraceEditor;
import com.onyem.jtracer.reader.ui.util.Messages;
import com.onyem.jtracer.reader.ui.util.Runtime;
import com.onyem.jtracer.reader.ui.util.SWTResourceManager;

public class WelcomeIntroPart extends IntroPart {
  private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
  private final IImageManager imageManager;

  private Control focusControl;

  public WelcomeIntroPart() {
    imageManager = Activator.getImageManager();
  }

  public void standbyStateChanged(boolean standby) {
  }

  @Override
  public void createPartControl(Composite container) {
    ScrolledForm form = formToolkit.createScrolledForm(container);
    formToolkit.paintBordersFor(form);
    createCenterControl(form.getBody());
  }

  private void createCenterControl(Composite container) {
    ColumnLayout layout = new ColumnLayout();
    layout.maxNumColumns = 1;
    container.setLayout(layout);

    Label imageLabel = formToolkit.createLabel(container, "", SWT.NONE);
    Image image = imageManager.getImage(IImageManager.LOGO);
    imageLabel.setImage(image);
    setCenterLayoutData(imageLabel);

    // Spacer
    formToolkit.createLabel(container, "", SWT.NONE);

    FormText onyemDescriptionText = formToolkit
        .createFormText(container, false);
    onyemDescriptionText.setText(
        "<form><p>Visualize your trace diagrams</p></form>", true, false);

    formToolkit.paintBordersFor(onyemDescriptionText);
    setFont(onyemDescriptionText, 14);
    setCenterLayoutData(onyemDescriptionText);

    // Spacer
    formToolkit.createLabel(container, "", SWT.NONE);
    formToolkit.createLabel(container, "", SWT.NONE);

    FormText getStartedText = formToolkit.createFormText(container, false);
    formToolkit.paintBordersFor(getStartedText);
    getStartedText.setText(
        "<form><p>Get started by opening samples ...</p></form>", true, false);
    setFont(getStartedText, 14);
    setCenterLayoutData(getStartedText);

    Composite samplesComposite = new Composite(container, SWT.NONE);
    formToolkit.paintBordersFor(samplesComposite);
    samplesComposite.setBackground(SWTResourceManager.getWhiteColor());
    createSamplesControl(samplesComposite);
    setCenterLayoutData(samplesComposite);

    formToolkit.createLabel(container, "", SWT.NONE);
    formToolkit.createLabel(container, "", SWT.NONE);
  }

  private void setCenterLayoutData(Control control) {
    ColumnLayoutData descriptionLayout = new ColumnLayoutData();
    descriptionLayout.horizontalAlignment = ColumnLayoutData.CENTER;
    control.setLayoutData(descriptionLayout);
  }

  private void createSamplesControl(Composite samplesComposite) {
    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.horizontalSpacing = 10;
    samplesComposite.setLayout(gridLayout);
    createSamplesRow("helloworld", "Simple helloworld program ",
        samplesComposite);
    createSamplesRow("threads", "Multiple threads", samplesComposite);
    createSamplesRow("junit", "Explore JUnit internals", samplesComposite);
    createSamplesRow("guice", "Explore Guice internals", samplesComposite);
  }

  private void createSamplesRow(final String nameText, String descriptionText,
      Composite samplesComposite) {
    FormText name = formToolkit.createFormText(samplesComposite, false);
    formToolkit.paintBordersFor(name);
    name.setText(nameText, false, false);

    FormText description = formToolkit.createFormText(samplesComposite, false);
    formToolkit.paintBordersFor(description);
    description.setText(descriptionText, false, false);

    Button openButton = formToolkit.createButton(samplesComposite,
        Messages.OPEN_LABEL, SWT.NONE);
    openButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        open();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        open();
      }

      private void open() {
        IWorkbenchWindow window = getIntroSite().getWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        if (page != null) {
          File file = Runtime.getSamplePath(nameText);
          if (file != null) {

            try {
              // The Welcome view should be the active part
              IWorkbenchPart activePart = page.getActivePart();
              IViewPart viewPart = (IViewPart) activePart;
              page.hideView(viewPart);

              IEditorInput editorInput = new FileEditorInput(file);
              page.openEditor(editorInput, TraceEditor.ID);
            } catch (PartInitException e) {
              String message = Messages.ERROR_OPENING_TRACE;
              Activator.logError(message, e);
              ErrorDialog dialog = new ErrorDialog(window.getShell(), message,
                  e);
              dialog.open();
            }
          }
        }
      }

    });
    if (focusControl == null) {
      focusControl = openButton;
    }

  }

  @Override
  public void setFocus() {
    if (focusControl != null) {
      focusControl.setFocus();
    }
  }

  private void setFont(FormText formText, int newSize) {
    FontData[] fontData = formText.getFont().getFontData();
    for (int i = 0; i < fontData.length; ++i) {
      fontData[i].setHeight(newSize);
    }
    final Font newFont = new Font(Display.getDefault(), fontData);
    formText.setFont(newFont);

    // Since you created the font, you must dispose it
    formText.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        newFont.dispose();
      }
    });
  }
}
