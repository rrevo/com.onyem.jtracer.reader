package com.onyem.jtracer.reader.ui.editors;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.dialogs.ErrorDialog;
import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;
import com.onyem.jtracer.reader.ui.editors.trace.ui.SimpleFigureCanvas;
import com.onyem.jtracer.reader.ui.editors.trace.ui.SummaryComposite;
import com.onyem.jtracer.reader.ui.editors.trace.ui.TraceResultClient;
import com.onyem.jtracer.reader.ui.editors.trace.ui.figure.EventTraceFigure;
import com.onyem.jtracer.reader.ui.factory.TraceFactory;
import com.onyem.jtracer.reader.ui.util.Messages;
import com.onyem.jtracer.reader.ui.util.SWTResourceManager;
import com.onyem.jtracer.reader.ui.util.SWTUtils;

public class TraceEditor extends EditorPart implements TraceResultClient {

  public static final String ID = "com.onyem.jtracer.reader.ui.editors.TraceEditor";

  // Model fields
  private FileEditorInput fileEditorInput;
  private Trace trace;

  // UI fields
  private Composite editorParent;
  private CTabFolder tabFolder;
  private final Map<String, CTabItem> tabItems;

  private Label loadingLabel;

  public TraceEditor() {
    tabItems = new LinkedHashMap<String, CTabItem>();
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
  }

  @Override
  public void doSaveAs() {
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    setSite(site);
    setInput(input);
    fileEditorInput = (FileEditorInput) input;
    setPartName(fileEditorInput.getName());

  }

  @Override
  public void createPartControl(Composite parent) {
    this.editorParent = parent;
    createLoadingPanel(parent);
    asyncCreateTrace();
  }

  private void createLoadingPanel(Composite parent) {
    loadingLabel = new Label(parent, SWT.NONE);
    loadingLabel.setText(Messages.LOADING_LABEL);
  }

  private void asyncCreateTrace() {
    String applicationPath = fileEditorInput.getPath();
    File file = new File(applicationPath);
    if (file.isFile()) {
      applicationPath = file.getParentFile().getPath();
    }

    TraceFactory traceFactory = Activator.getTraceFactory();
    traceFactory.create(applicationPath, this);
  }

  @Override
  public void setTraceResult(final Trace traceResult, final Exception exception) {

    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {

        if (exception != null) {
          loadingLabel.setText(Messages.ERROR_OPENING_TRACE);
          Activator.logError(Messages.ERROR_OPENING_TRACE, exception);
          ErrorDialog errorDialog = new ErrorDialog(getSite().getShell(),
              Messages.ERROR_OPENING_TRACE, exception);
          errorDialog.open();

        } else {
          trace = traceResult;

          loadingLabel.dispose();

          tabFolder = new CTabFolder(editorParent, SWT.BOTTOM);
          createSummaryTab();

          editorParent.layout();
        }
      }
    });
  }

  private void createSummaryTab() {
    SWTUtils.assertDisplayThread();

    CTabItem tabItemSummary = new CTabItem(tabFolder, SWT.NONE);
    SummaryComposite summaryComposite = new SummaryComposite(tabFolder,
        Activator.getImageManager(), trace, this);
    tabItemSummary.setText(Messages.SUMMARY_LABEL);
    tabItemSummary.setControl(summaryComposite);
    tabFolder.setSelection(tabItemSummary);
  }

  public void createEventTab(String eventFileName) {
    SWTUtils.assertDisplayThread();

    if (tabItems.containsKey(eventFileName)) {
      tabFolder.setSelection(tabItems.get(eventFileName));
    } else {
      buildEventTab(eventFileName);
    }
  }

  private void buildEventTab(final String eventFileName) {
    // Make closeable tabs when there are more than 1 event logs
    final boolean closeable = trace.getTriggerEventFileNames().size() > 1;

    CTabItem tabItemEvent = new CTabItem(tabFolder, closeable ? SWT.CLOSE
        : SWT.NONE);
    tabItemEvent.setText(Messages.TRACE_LABEL + " - " + eventFileName);
    EventTraceFigure figure = new EventTraceFigure(trace, eventFileName,
        Activator.getQueue());
    SimpleFigureCanvas figureCanvas = new SimpleFigureCanvas(tabFolder);
    figureCanvas.setBackground(SWTResourceManager.getWhiteColor());
    figureCanvas.setContents(figure);
    tabItemEvent.setControl(figureCanvas);

    if (closeable) {
      tabItemEvent.addDisposeListener(new DisposeListener() {

        @Override
        public void widgetDisposed(DisposeEvent arg0) {
          handleEventTabClose(eventFileName);
        }
      });
    }

    tabFolder.setSelection(tabItemEvent);
    tabItems.put(eventFileName, tabItemEvent);
  }

  private void handleEventTabClose(String eventFileName) {
    CTabItem tabItem = tabItems.remove(eventFileName);
    if (tabItem != null && !tabItem.isDisposed()) {
      tabItem.dispose();
    }
  }

  @Override
  public void setFocus() {
    if (tabFolder != null) {
      tabFolder.getSelection().getControl().setFocus();
    }
  }

  @Override
  public void dispose() {
    if (trace != null) {
      try {
        trace.close();
      } catch (IOException e) {
        Activator.logError(Messages.ERROR_CLOSING_TRACE, e);
      }
    }
    super.dispose();
  }

}
