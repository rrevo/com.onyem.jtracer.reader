package com.onyem.jtracer.reader.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.onyem.jtracer.reader.ui.util.Messages;
import com.onyem.jtracer.reader.ui.util.StackTraceUtil;

public class ErrorDialog {

  private final Shell shell;
  private final String message;
  private final Throwable throwable;

  public ErrorDialog(Shell parent, String message, Throwable throwable) {
    this.shell = parent;
    this.message = message;
    this.throwable = throwable;
  }

  public void open() {
    final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
        | SWT.APPLICATION_MODAL | SWT.RESIZE);
    dialog.setText(Messages.ERROR_HEADER);
    dialog.setLayout(new GridLayout(1, true));

    Label label = new Label(dialog, SWT.NONE);
    label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    label.setText(message);

    ScrolledComposite composite = new ScrolledComposite(dialog, SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    composite.setExpandHorizontal(true);
    composite.setExpandVertical(true);

    Text text = new Text(composite, SWT.READ_ONLY | SWT.MULTI);
    text.setText(StackTraceUtil.asString(throwable));
    composite.setContent(text);
    composite.setMinSize(text.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    Button ok = new Button(dialog, SWT.PUSH);
    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    ok.setText(Messages.CLOSE);
    ok.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        dialog.close();
      }
    });

    dialog.setDefaultButton(ok);

    dialog.pack();
    dialog.open();
  }
}
