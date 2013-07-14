package com.onyem.jtracer.reader.ui.editors.trace.ui;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.onyem.jtracer.reader.db.IJdbcHelper;
import com.onyem.jtracer.reader.db.ResultRowMapper;
import com.onyem.jtracer.reader.queue.IQueueService;
import com.onyem.jtracer.reader.ui.editors.TraceEditor;
import com.onyem.jtracer.reader.ui.editors.trace.model.Trace;

public class QueryDebugComposite extends Composite {

  private final IQueueService queueService;
  private final IJdbcHelper helper;

  private final Text queryText;
  private final Text resultsText;

  public QueryDebugComposite(Composite parent,
      final IQueueService queueService, final Trace trace,
      TraceEditor traceEditor) {
    super(parent, SWT.NONE);

    this.queueService = queueService;
    this.helper = trace.getJdbcHelper();

    setLayout(new FillLayout());

    SashForm sashForm = new SashForm(this, SWT.VERTICAL);

    ScrolledComposite queryScroll = new ScrolledComposite(sashForm, SWT.BORDER
        | SWT.V_SCROLL);
    queryScroll.setExpandHorizontal(true);
    queryScroll.setExpandVertical(true);

    Composite queryComposite = new Composite(queryScroll, SWT.NONE);
    queryComposite.setLayout(new GridLayout(1, false));

    Label label = new Label(queryComposite, SWT.NONE);
    label.setText("Enter SQL Query");

    queryText = new Text(queryComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    GridData queryTextData = new GridData(SWT.FILL, SWT.FILL, true, false);
    queryTextData.heightHint = 5 * queryText.getLineHeight();
    queryText.setLayoutData(queryTextData);

    Button runButton = new Button(queryComposite, SWT.NONE);
    runButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        executeQuery();
      }
    });
    runButton
        .setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    runButton.setText("Run");

    queryScroll.setContent(queryComposite);
    queryScroll
        .setMinSize(queryComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    ScrolledComposite resultsScroll = new ScrolledComposite(sashForm,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    resultsScroll.setExpandHorizontal(true);
    resultsScroll.setExpandVertical(true);

    resultsText = new Text(resultsScroll, SWT.BORDER | SWT.READ_ONLY
        | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

    Font monospacedFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
    resultsText.setFont(monospacedFont);

    resultsScroll.setContent(resultsText);
    resultsScroll.setMinSize(resultsText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    sashForm.setWeights(new int[] { 1, 2 });
  }

  void executeQuery() {
    final String query = queryText.getText().trim();
    queueService.queueNow(new Runnable() {

      @Override
      public void run() {
        final Table queryResults = new Table();
        try {
          helper.query(query, new ResultRowMapper<Void>() {

            @Override
            public Void mapRow(ResultSet rs, int rowNum) throws SQLException {

              ResultSetMetaData metaData = rs.getMetaData();
              int columnCount = metaData.getColumnCount();

              if (!queryResults.isHeaderSet()) {
                queryResults.addHeaderRow();
                for (int i = 0; i < columnCount; i++) {
                  final int colIndex = i + 1;
                  queryResults.addHeaderColumn(metaData
                      .getColumnLabel(colIndex));
                }
              }

              queryResults.addRow();

              for (int i = 0; i < columnCount; i++) {
                final int colIndex = i + 1;
                final int columnType = metaData.getColumnType(colIndex);

                switch (columnType) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                  Integer in = rs.getInt(colIndex);
                  if (rs.wasNull()) {
                    queryResults.addColumn(null);
                  } else {
                    queryResults.addColumn(in + "");
                  }
                  break;

                case Types.BIGINT:
                  Long l = rs.getLong(colIndex);
                  if (rs.wasNull()) {
                    queryResults.addColumn(null);
                  } else {
                    queryResults.addColumn(l + "");
                  }
                  break;

                case Types.CHAR:
                case Types.VARCHAR:
                  String varChar = rs.getString(colIndex);
                  if (rs.wasNull()) {
                    queryResults.addColumn(null);
                  } else {
                    queryResults.addColumn(varChar + "");
                  }
                  break;

                case Types.BOOLEAN:
                  boolean bool = rs.getBoolean(colIndex);
                  if (rs.wasNull()) {
                    queryResults.addColumn(null);
                  } else {
                    queryResults.addColumn(bool + "");
                  }
                  break;

                default:
                  queryResults.addColumn("TODO: COL " + columnType);
                }
              }
              return null;
            }
          });

          renderResults(getResults(queryResults));
        } catch (RuntimeException re) {
          renderResults(getException(re.getCause()));
        }
      }
    });
  }

  private String getResults(final Table queryResults) {

    List<Integer> colWidths = queryResults.colWidths;

    StringBuffer sb = new StringBuffer();
    int rowIndex = 0;
    if (queryResults.isHeaderSet()) {
      getRow(sb, colWidths, queryResults.results.get(rowIndex));
      rowIndex++;
      for (Integer colWidth : colWidths) {
        for (int i = 0; i <= colWidth; i++) {
          sb.append("-");
        }
      }
      sb.append("\n");
    }

    for (; rowIndex < queryResults.results.size(); rowIndex++) {
      List<String> row = queryResults.results.get(rowIndex);
      getRow(sb, colWidths, row);
    }
    return sb.toString();
  }

  void getRow(StringBuffer sb, List<Integer> colWidths, List<String> row) {
    for (int colIndex = 0; colIndex < row.size(); colIndex++) {
      String column = row.get(colIndex);
      sb.append(String.format("%-" + colWidths.get(colIndex) + "s", column));
      sb.append("|");
    }
    sb.append("\n");
  }

  private String getException(Throwable e) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Error executing query\n\n");
    buffer.append(e.toString());
    return buffer.toString();
  }

  private void renderResults(final String data) {
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        resultsText.setText("");
        resultsText.setText(data);
      }
    });
  }

  private static class Table {

    private final List<List<String>> results = new ArrayList<List<String>>();
    private final List<Integer> colWidths = new ArrayList<Integer>();

    private boolean headerRow = false;
    private int currentRowIndex = 1;
    private int currentColIndex = 0;
    private List<String> currentRow;

    void addRow() {
      currentRow = new ArrayList<String>();
      results.add(currentRow);
      currentColIndex = 0;
      addColumn(currentRowIndex + "");
      currentRowIndex++;
    }

    public void addHeaderRow() {
      currentRow = new ArrayList<String>();
      results.add(0, currentRow);
      currentColIndex = 0;
      addColumn("");
      headerRow = true;
    }

    public void addHeaderColumn(String columnLabel) {
      addColumn(columnLabel);
    }

    public boolean isHeaderSet() {
      return headerRow;
    }

    void addColumn(String data) {
      if (data == null) {
        data = "NULL";
      }
      data = data.trim();
      int len = data.length();

      if (colWidths.size() == currentColIndex) {
        colWidths.add(len);
      } else if (colWidths.get(currentColIndex) < len) {
        colWidths.set(currentColIndex, len);
      }
      currentRow.add(data);
      currentColIndex++;
    }
  }
}
