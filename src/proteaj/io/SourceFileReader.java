package proteaj.io;

import proteaj.error.FileIOError;

import java.io.*;

import org.eclipse.lsp4j.TextDocumentItem;

public class SourceFileReader {
  public SourceFileReader(File file) throws FileIOError {
    try {
      reader = new BufferedReader(new FileReader(file));
      c = reader.read();
      line = 1;
      filePath = file.getPath();
    } catch (IOException e) {
      throw new FileIOError("can't read source file", filePath, 0);
    }
  }

  public SourceFileReader(TextDocumentItem doc) throws FileIOError {
    try {
      reader = new BufferedReader(new StringReader(doc.getText()));
      c = reader.read();
      line = 1;
      filePath = doc.getUri();
    } catch (IOException e) {
      throw new FileIOError("can't read source file", filePath, 0);
    }
  }

  public String getFilePath() {
    return filePath;
  }

  public int getLine() {
    return line;
  }

  public boolean hasNext() {
    return c != -1;
  }

  public int lookahead() {
    return c;
  }

  public char next() throws FileIOError {
    try {
      char ret = (char) c;
      c = reader.read();
      if (ret == '\n')
        line++;
      return ret;
    } catch (IOException e) {
      throw new FileIOError("file read error", filePath, line);
    }
  }

  public void nextLine() throws FileIOError {
    while (hasNext()) {
      if (next() == '\n')
        break;
    }
  }

  private int c;
  private int line;
  private String filePath;
  private BufferedReader reader;
}
