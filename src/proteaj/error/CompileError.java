package proteaj.error;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.Position;

public abstract class CompileError extends Exception {
  public CompileError(String message, String file, int line) {
    super(message);
    this.file = file;
    this.line = line;
  }

  public CompileError at(int line) {
    this.line = line;
    return this;
  }

  public String getFile() {
    return file;
  }

  public int getLine() {
    return line;
  }

  public Diagnostic toDiagnostic() {
    Diagnostic diagnostic = new Diagnostic();
    diagnostic.setMessage(getMessage());
    diagnostic.setSeverity(DiagnosticSeverity.Error);
    diagnostic.setSource("proteaj");
    Range range = new Range();
    range.setStart(new Position(line - 1, 0));
    range.setEnd(new Position(line - 1, 0));
    diagnostic.setRange(range);
    return diagnostic;
  }

  public abstract String getKind();

  private String file;
  private int line;

  private static final long serialVersionUID = 1L;
}
