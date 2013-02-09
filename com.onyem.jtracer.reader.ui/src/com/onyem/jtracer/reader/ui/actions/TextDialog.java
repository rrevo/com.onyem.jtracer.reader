package com.onyem.jtracer.reader.ui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.util.Constants;
import com.onyem.jtracer.reader.ui.util.Messages;
import com.onyem.jtracer.reader.ui.util.URLDownloader;

public class TextDialog {

  private final IQueueService queueService;
  private final Shell shell;
  private final String text;
  private final String message;
  private int width = 400;

  public TextDialog(IQueueService queueService, Shell parent, String text,
      String message) {
    this.queueService = queueService;
    this.shell = parent;
    this.text = text;
    this.message = message;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void open() {
    final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
        | SWT.APPLICATION_MODAL);
    dialog.setText(text);
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = 10;
    formLayout.marginHeight = 10;
    formLayout.spacing = 10;
    dialog.setLayout(formLayout);

    Button ok = new Button(dialog, SWT.PUSH);
    ok.setText(Messages.OK);
    FormData data = new FormData();
    data.width = 60;
    data.right = new FormAttachment(100, 0);
    data.bottom = new FormAttachment(100, 0);
    ok.setLayoutData(data);
    ok.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        dialog.close();
      }
    });

    Link link = new Link(dialog, SWT.NONE);
    asyncSetVersionMessage(link);
    data = new FormData();
    link.setLayoutData(data);

    data.width = width;
    data.right = new FormAttachment(100, 0);
    data.bottom = new FormAttachment(ok, 0, SWT.DEFAULT);

    Label label2 = new Label(dialog, SWT.NONE);
    label2.setText(message);
    data = new FormData();
    label2.setLayoutData(data);

    data.width = width;
    data.right = new FormAttachment(100, 0);
    data.bottom = new FormAttachment(link, 0, SWT.DEFAULT);

    dialog.setDefaultButton(ok);

    dialog.pack();
    dialog.open();
  }

  private void asyncSetVersionMessage(final Link link) {
    queueService.queueNow(new Runnable() {

      @Override
      public void run() {
        String displayMessageMutable = Messages.VERSION_UNKNOWN;
        try {
          String versionPage = URLDownloader.download(Constants.URL_VERSION);
          if (versionPage.contains(Constants.AGENT_VERSION_PAGE_DATA)) {
            displayMessageMutable = Messages.VERSION_AT_LATEST;
          } else {
            displayMessageMutable = Messages.VERSION_NEW_AVAILABLE;
          }
        } catch (Exception e) {
          // Ignore any exception
        }
        final String displayMessage = displayMessageMutable;
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            if (link != null && !link.isDisposed()) {
              link.setText(displayMessage);
            }
          }
        });
      }
    });
  }
}
