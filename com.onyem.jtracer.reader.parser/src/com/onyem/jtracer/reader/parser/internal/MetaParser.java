package com.onyem.jtracer.reader.parser.internal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onyem.jtracer.reader.annotations.Service;
import com.onyem.jtracer.reader.parser.IMetaParser;

@Service
@ThreadSafe
public class MetaParser implements IMetaParser {

  private final static Pattern SPLIT_PATTERN = Pattern.compile("\\|");
  private final static int CHARS_PER_LINE = 25;

  // Tags
  private final static String OPEN = "<";
  private final static String SEPARATOR = "|";

  // Events
  private final static String INDEX_CLASS = "ic"; // Class index
  private final static String INDEX_METHOD = "im"; // Method index

  private final String name;
  private final String[] startTags;
  private final RandomAccessFile metaFile;
  private final long metaFileLength;

  private Line firstLine;

  @Inject
  public MetaParser(@Assisted String name, @Assisted RandomAccessFile metaFile) {
    this.name = name;
    this.metaFile = metaFile;
    this.startTags = getStartTags();
    try {
      metaFileLength = metaFile.length();
      metaFile.seek(0);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private String[] getStartTags() {
    Set<String> localStartTags = new HashSet<String>();
    localStartTags.add(OPEN + INDEX_CLASS + SEPARATOR);
    localStartTags.add(OPEN + INDEX_METHOD + SEPARATOR);
    return localStartTags.toArray(new String[0]);
  }

  @Override
  public synchronized void close() throws IOException {
    metaFile.close();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public synchronized String getLine(final long searchIndex) {
    if (searchIndex < 0) {
      throw new IllegalArgumentException();
    }
    try {
      // Initialize firstLine if needed
      if (firstLine == null) {
        firstLine = getNextLine(metaFileLength);
      }
      // Scan the whole file for the searchIndex
      return findLineByIndex(searchIndex, firstLine, metaFileLength);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * Algorithm notes:
   * start always has an index <= searchIndex
   * We try to guess a new hint index/line within the (start, end) positions
   * if (hint <= searchIndex) then the search is narrowed between (hint, end)
   * if (hint > searchIndex) then the search is narrowed between (start, hint)
   */
  private String findLineByIndex(final long searchIndex, final Line startLine,
      final long endPosition) throws IOException {

    // Exact match
    if (searchIndex == startLine.index) {
      return startLine.data;
    }
    // startIndex is beyond the search Index
    if (searchIndex < startLine.index) {
      throw new RuntimeException();
    }

    final Line hintLine = getHintLine(searchIndex, startLine, endPosition);
    if (hintLine == null || hintLine.startPosition >= endPosition) {
      throw new RuntimeException();
    }
    if (hintLine.index <= searchIndex) {
      return findLineByIndex(searchIndex, hintLine, endPosition);
    } else {
      return findLineByIndex(searchIndex, startLine, hintLine.startPosition);
    }
  }

  private Line getHintLine(long searchIndex, Line startLine, long endPosition)
      throws IOException {
    final long indexDiff = searchIndex - startLine.index;
    // If the jump is too high then the reader becomes a linear walk
    final long hintPosition = startLine.startPosition
        + (indexDiff * CHARS_PER_LINE);
    Line hintLine = null;
    if (hintPosition < endPosition) {
      metaFile.seek(hintPosition);
      hintLine = getNextLine(endPosition);
    }
    if (hintLine == null || hintLine.startPosition == endPosition) {
      metaFile.seek(startLine.endPosition);
      hintLine = getNextLine(endPosition);
    }
    return hintLine;
  }

  private Line getNextLine(final long endPosition) throws IOException {
    long startPosition = metaFile.getFilePointer();
    String line = metaFile.readLine();
    if (line == null) {
      throw new RuntimeException();
    }
    if (!isValidLineStart(line)) {
      // Since the startPosition was in the middle of a line
      // skip the rest of this line
      startPosition = metaFile.getFilePointer();
      if (startPosition >= endPosition) {
        return null;
      }
      line = metaFile.readLine();
    }
    long lineEndPosition = metaFile.getFilePointer();
    return new Line(startPosition, lineEndPosition, line);
  }

  private boolean isValidLineStart(String line) {
    for (String startTag : startTags) {
      if (line.startsWith(startTag)) {
        return true;
      }
    }
    return false;
  }

  @Immutable
  private static class Line {

    public final long startPosition;
    public final long endPosition;
    public final String data;
    public final long index;

    public Line(long startPosition, long endPosition, String line) {
      this.startPosition = startPosition;
      this.endPosition = endPosition;
      this.data = line;
      String[] dataParts = SPLIT_PATTERN.split(line.substring(1,
          line.length() - 1));
      index = Long.parseLong(dataParts[1]);
    }

    @Override
    public String toString() {
      return "Line [startPosition=" + startPosition + ", endPosition="
          + endPosition + ", data=" + data + ", index=" + index + "]";
    }

  }
}
