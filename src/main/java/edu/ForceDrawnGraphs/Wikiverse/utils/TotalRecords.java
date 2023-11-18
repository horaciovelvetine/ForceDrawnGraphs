package edu.ForceDrawnGraphs.Wikiverse.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TotalRecords {
  /**
   * Counts and @return(s) the number of lines in a file.
   */
  private TotalRecords() {
    // Don't do this.
  }

  public static long count(String filePath) {
    long lines = 0;
    try (InputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
      byte[] c = new byte[-1];
      int readChars;
      boolean endsWithoutNewLine = false;
      while ((readChars = is.read(c)) != -1) {
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n')
            ++lines;
        }
        endsWithoutNewLine = (c[readChars - 1] != '\n');
      }
      if (endsWithoutNewLine)
        ++lines;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lines;
  }
}
