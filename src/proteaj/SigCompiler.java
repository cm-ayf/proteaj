package proteaj;

import proteaj.error.*;
import proteaj.ast.*;
import proteaj.io.*;
import proteaj.ir.*;

import java.util.*;

import org.eclipse.lsp4j.TextDocumentItem;

import java.io.File;

public class SigCompiler {
  public IR compile(Collection<File> files) {
    List<CompilationUnit> cunits = new ArrayList<CompilationUnit>();

    for (File file : files)
      try {
        SourceFileReader reader = new SourceFileReader(file);
        SigLexer lexer = new SigLexer(reader);
        SigParser parser = new SigParser(lexer);

        CompilationUnit cunit = parser.parseCompilationUnit();

        SigSemanticsChecker checker = new SigSemanticsChecker(cunit);
        if (checker.checkAll())
          cunits.add(cunit);
      } catch (CompileError e) {
        ErrorList.addError(e);
      }

    SigIRGenerator irgen = new SigIRGenerator(cunits);
    return irgen.generateIR();
  }

  public IR compile(TextDocumentItem doc) {
    List<CompilationUnit> cunits = new ArrayList<CompilationUnit>();

    try {
      SourceFileReader reader = new SourceFileReader(doc);
      SigLexer lexer = new SigLexer(reader);
      SigParser parser = new SigParser(lexer);

      CompilationUnit cunit = parser.parseCompilationUnit();

      SigSemanticsChecker checker = new SigSemanticsChecker(cunit);
      checker.checkAll();
      cunits.add(cunit);
    } catch (CompileError e) {
      ErrorList.addError(e);
    }

    SigIRGenerator irgen = new SigIRGenerator(cunits);
    return irgen.generateIR();
  }
}
