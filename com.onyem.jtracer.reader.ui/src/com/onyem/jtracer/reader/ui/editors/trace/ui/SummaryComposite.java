package com.onyem.jtracer.reader.ui.editors.trace.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.onyem.jtracer.reader.meta.IClass;
import com.onyem.jtracer.reader.meta.IMethod;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.Activator;
import com.onyem.jtracer.reader.ui.IImageManager;
import com.onyem.jtracer.reader.ui.editors.TraceEditor;
import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;
import com.onyem.jtracer.reader.ui.editors.trace.model.rules.ClassNameRule;
import com.onyem.jtracer.reader.ui.util.Constants;
import com.onyem.jtracer.reader.ui.util.Messages;

public class SummaryComposite extends Composite {

  private final IQueueService queueService;
  private final Trace trace;

  private final TraceEditor traceEditor;
  private final FormToolkit formToolkit;

  public SummaryComposite(Composite parent, final IImageManager imageManager,
      final Trace trace, TraceEditor traceEditor) {
    super(parent, SWT.NONE);

    this.queueService = Activator.getQueue();
    this.trace = trace;

    this.traceEditor = traceEditor;
    this.formToolkit = new FormToolkit(Display.getDefault());

    setLayout(new FillLayout());

    ScrolledForm scrolledForm = formToolkit.createScrolledForm(this);
    formToolkit.paintBordersFor(scrolledForm);
    scrolledForm.setText("JTrace");

    ColumnLayout columnLayout = new ColumnLayout();
    columnLayout.maxNumColumns = 1;

    final Composite holderComposite = scrolledForm.getBody();
    holderComposite.setLayout(columnLayout);

    Map<String, Long> triggerEventFiles = trace.getTriggerEventFileNames();

    if (triggerEventFiles.isEmpty()) {
      Group group = createGroup(holderComposite, "JTrace Event log");
      createEventFilesTable(group, formToolkit, Constants.EVENT_FILE_NAME);
    } else {
      Group group = createGroup(holderComposite, "JTrace Event logs");
      createEventFilesTable(group, formToolkit, triggerEventFiles);
    }

    if (!trace.getClassTraceChecker().getRules().isEmpty()) {
      Group group = createGroup(holderComposite, "Selector Rules");
      createSelectorTable(group, formToolkit, trace.getClassTraceChecker()
          .getRules(), imageManager);
    }

    if (!trace.getTriggerData().isEmpty()) {
      Group group = createGroup(holderComposite, "Trigger methods");
      createTriggerTable(group, formToolkit,
          trace.getTriggerData().toArray(new String[0][]));
    }

    {
      List<String[]> propertiesData = trace.getProperties();
      Group group = createGroup(holderComposite, "Properties");
      createPropertiesTable(group, formToolkit,
          propertiesData.toArray(new String[0][]));
    }

    {
      List<String[]> propertiesData = trace.getOtherProperties();
      if (!propertiesData.isEmpty()) {
        Group group = createGroup(holderComposite, "Other Properties");
        createPropertiesTable(group, formToolkit,
            propertiesData.toArray(new String[0][]));
      }
    }

  }

  private Group createGroup(Composite parent, String label) {
    Group group = new Group(parent, SWT.NONE);
    group.setText(label);
    formToolkit.adapt(group);
    formToolkit.paintBordersFor(group);

    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = 5;
    formLayout.marginWidth = 5;
    group.setLayout(formLayout);

    return group;
  }

  private void createEventFilesTable(Composite parent, FormToolkit formToolkit,
      final String defaultEventFileName) {

    /*
     * TableViewers are not used since we need to add a widget in the last
     * column
     */
    final Table table = new Table(parent, SWT.BORDER);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    String[] titles = { "File", "" };
    int[] widths = { 500, 100 };
    createTableColumns(table, titles, widths);

    final TableItem tableItem = new TableItem(table, SWT.NONE);
    tableItem.setText(0, defaultEventFileName);

    TableEditor editor = new TableEditor(table);
    Button button = new Button(table, SWT.NONE);
    button.setText(Messages.OPEN_LABEL);
    button.pack();
    editor.minimumWidth = button.getSize().x;
    editor.horizontalAlignment = SWT.LEFT;
    editor.setEditor(button, tableItem, 1);

    button.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent arg0) {
        traceEditor.createEventTab(defaultEventFileName);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent arg0) {
        traceEditor.createEventTab(defaultEventFileName);
      }
    });

    FormData formData = new FormData();
    formData.left = new FormAttachment(0);
    formData.top = new FormAttachment(0);
    formData.right = new FormAttachment(100);
    formData.bottom = new FormAttachment(100);
    table.setLayoutData(formData);
  }

  /*
   * NOTE trace.isValid() check is *not* made because multiple
   * trigger files can only be created when something was traced
   */
  private void createEventFilesTable(Composite parent, FormToolkit formToolkit,
      Map<String, Long> triggerEventFiles) {

    assert !triggerEventFiles.isEmpty();

    FormText headerText = formToolkit.createFormText(parent, false);
    headerText
        .setText(
            "<form><p>Multiple JTrace Event logs generated per invocation of the Trigger method</p></form>",
            true, false);

    /*
     * TableViewers are not used since we need to add a widget in the last
     * column
     */
    Table table = new Table(parent, SWT.BORDER);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    String[] titles = { "Class", "Method", "File", "" };
    int[] widths = { 300, 100, 100, 75 };
    createTableColumns(table, titles, widths);

    final List<RowDataHolder> labels = new ArrayList<RowDataHolder>();
    for (String eventFile : triggerEventFiles.keySet()) {
      labels.add(addPartialRow(table, formToolkit, eventFile,
          triggerEventFiles.get(eventFile)));
    }

    for (final RowDataHolder rowDataHolder : labels) {
      queueService.queueNow(new Runnable() {
        @Override
        public void run() {

          final IMethod method = trace.getMetaService().getMethodByMetaId(
              rowDataHolder.methodIndex);
          final IClass clazz = trace.getMetaService().getMethodClass(method);

          Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
              rowDataHolder.item.setText(0, clazz.getCompleteName());
              rowDataHolder.item.setText(1, method.getName());
            }
          });
        }
      });
    }

    Composite spacerComposite = new Composite(parent, SWT.NONE);
    formToolkit.adapt(spacerComposite);
    formToolkit.paintBordersFor(spacerComposite);
    spacerComposite.setLayout(new RowLayout(SWT.VERTICAL));

    new Label(spacerComposite, SWT.NONE);
    new Label(spacerComposite, SWT.NONE);
    for (int i = 0; i < triggerEventFiles.size(); i++) {
      new Label(spacerComposite, SWT.NONE);
    }

    FormData formData = new FormData();
    formData.left = new FormAttachment(0);
    formData.right = new FormAttachment(100);
    headerText.setLayoutData(formData);

    formData = new FormData();
    formData.top = new FormAttachment(headerText);
    formData.right = new FormAttachment(100);
    formData.bottom = new FormAttachment(100);
    spacerComposite.setLayoutData(formData);

    formData = new FormData();
    formData.top = new FormAttachment(headerText);
    formData.left = new FormAttachment(0);
    formData.right = new FormAttachment(spacerComposite);
    formData.bottom = new FormAttachment(100);
    table.setLayoutData(formData);
  }

  private void createTableColumns(final Table table, String[] titles,
      int[] widths) {
    for (int i = 0; i < widths.length; i++) {
      final TableColumn column = new TableColumn(table, SWT.NONE);
      column.setText(titles[i]);
      column.setWidth(widths[i]);
      column.setResizable(true);
    }
  }

  private RowDataHolder addPartialRow(Table table, FormToolkit formToolkit,
      final String eventFile, long methodIndex) {
    TableItem tableItem = new TableItem(table, SWT.NONE);
    tableItem.setText(0, Messages.LOADING_LABEL);
    tableItem.setText(1, Messages.LOADING_LABEL);
    tableItem.setText(2, eventFile);

    TableEditor editor = new TableEditor(table);
    Button button = new Button(table, SWT.NONE);
    button.setText(Messages.OPEN_LABEL);
    button.pack();
    formToolkit.adapt(button, true, true);
    editor.minimumWidth = button.getSize().x;
    editor.horizontalAlignment = SWT.LEFT;
    editor.setEditor(button, tableItem, 3);

    button.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent arg0) {
        traceEditor.createEventTab(eventFile);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent arg0) {
        traceEditor.createEventTab(eventFile);
      }
    });

    RowDataHolder labelHolder = new RowDataHolder(methodIndex, tableItem);
    return labelHolder;
  }

  private static class RowDataHolder {
    final long methodIndex;
    private final TableItem item;

    public RowDataHolder(long methodIndex, TableItem item) {
      this.methodIndex = methodIndex;
      this.item = item;
    }
  }

  private void createSelectorTable(Composite parent, FormToolkit formToolkit,
      Set<ClassNameRule> selectorRules, IImageManager imageManager) {

    assert !selectorRules.isEmpty();

    FormText headerText = formToolkit.createFormText(parent, false);
    headerText
        .setText(
            "<form><p>Selector rules are used to add or remove classes from the JTrace</p></form>",
            true, false);

    final TableViewer tableViewer = new TableViewer(parent);
    Table table = tableViewer.getTable();
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    String[] titles = { "Type", "Rule Expression" };
    int[] widths = { 40, 300 };
    createTableColumns(tableViewer, titles, widths);

    tableViewer.setContentProvider(new ArrayContentProvider());
    tableViewer.setLabelProvider(new SelectorLabelProvider(imageManager));
    tableViewer.setInput(selectorRules);

    Composite spacerComposite = new Composite(parent, SWT.NONE);
    formToolkit.adapt(spacerComposite);
    formToolkit.paintBordersFor(spacerComposite);
    spacerComposite.setLayout(new RowLayout(SWT.VERTICAL));

    new Label(spacerComposite, SWT.NONE);
    new Label(spacerComposite, SWT.NONE);
    new Label(spacerComposite, SWT.NONE);
    new Label(spacerComposite, SWT.NONE);

    FormData formData = new FormData();
    formData.top = new FormAttachment(0);
    formData.left = new FormAttachment(0);
    formData.right = new FormAttachment(100);
    headerText.setLayoutData(formData);

    formData = new FormData();
    formData.top = new FormAttachment(headerText);
    formData.left = new FormAttachment(0);
    formData.right = new FormAttachment(100);
    formData.bottom = new FormAttachment(100);
    table.setLayoutData(formData);
  }

  private static void createTableColumns(final TableViewer tableViewer,
      String[] titles, int[] widths) {
    for (int i = 0; i < widths.length; i++) {
      TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer,
          SWT.NONE);
      final TableColumn column = viewerColumn.getColumn();
      column.setText(titles[i]);
      column.setWidth(widths[i]);
      column.setResizable(true);
    }
  }

  private class SelectorLabelProvider extends LabelProvider implements
      ITableLabelProvider {

    private final IImageManager imageManager;

    public SelectorLabelProvider(IImageManager imageManager) {
      this.imageManager = imageManager;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      ClassNameRule classNameRule = (ClassNameRule) element;
      switch (columnIndex) {
      case 0:
        if (classNameRule.isInclude()) {
          return imageManager.getImage(IImageManager.METHOD_ENTRY);
        } else {
          return imageManager.getImage(IImageManager.METHOD_EXIT);
        }
      case 1:
        return null;

      default:
        throw new IllegalArgumentException();
      }
    }

    @Override
    public String getColumnText(Object element, final int columnIndex) {
      ClassNameRule classNameRule = (ClassNameRule) element;
      switch (columnIndex) {
      case 0:
        return null;
      case 1:
        return classNameRule.getDisplayRegex();

      default:
        throw new IllegalArgumentException();
      }
    }
  }

  public void createTriggerTable(Composite parent, FormToolkit formToolkit,
      String[][] triggerData) {
    createDataTable(parent, formToolkit, "Class", 300, "Method", 100,
        triggerData);
  }

  public void createPropertiesTable(Composite parent, FormToolkit formToolkit,
      String[][] propertiesData) {
    createDataTable(parent, formToolkit, "Key", 100, "Value", 300,
        propertiesData);
  }

  private void createDataTable(Composite parent, FormToolkit formToolkit,
      String column1, int width1, String column2, int width2, String[][] data) {

    assert data.length > 0;

    TableViewer tableViewer = new TableViewer(parent);
    Table table = tableViewer.getTable();
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    String[] titles = { column1, column2 };
    createTableColumns(tableViewer, titles, new int[] { width1, width2 });

    tableViewer.setContentProvider(new ArrayContentProvider());
    tableViewer.setLabelProvider(new ArrayLabelProvider());
    tableViewer.setInput(data);

    FormData formData = new FormData();
    formData.top = new FormAttachment(0);
    formData.left = new FormAttachment(0);
    formData.right = new FormAttachment(100);
    formData.bottom = new FormAttachment(100);
    table.setLayoutData(formData);
  }

  private static class ArrayLabelProvider extends LabelProvider implements
      ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }

    @Override
    public String getColumnText(Object element, final int columnIndex) {
      String[] data = (String[]) element;
      switch (columnIndex) {
      case 0:
        return data[0];
      case 1:
        return data[1];

      default:
        throw new IllegalArgumentException();
      }
    }
  }
}
