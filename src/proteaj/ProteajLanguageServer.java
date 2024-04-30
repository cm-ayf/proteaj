package proteaj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.LogTraceParams;
import org.eclipse.lsp4j.PositionEncodingKind;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextDocumentSyncOptions;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import proteaj.error.CompileError;
import proteaj.error.ErrorList;
import proteaj.ir.IR;
import proteaj.tast.Program;

public class ProteajLanguageServer
    implements LanguageServer, LanguageClientAware, TextDocumentService, WorkspaceService {
  Map<String, IR> irs;
  Map<String, Program> programs;
  LanguageClient client;

  public ProteajLanguageServer() {
    irs = new java.util.HashMap<>();
    programs = new java.util.HashMap<>();
  }

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
    ServerCapabilities capabilities = new ServerCapabilities();
    capabilities.setPositionEncoding(PositionEncodingKind.UTF16);
    TextDocumentSyncOptions syncOptions = new TextDocumentSyncOptions();
    syncOptions.setOpenClose(true);
    capabilities.setTextDocumentSync(syncOptions);
    InitializeResult result = new InitializeResult(capabilities);
    return CompletableFuture.completedFuture(result);
  }

  @Override
  public void initialized(InitializedParams params) {
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    return CompletableFuture.completedFuture(new Object());
  }

  @Override
  public void exit() {
  }

  private void log(String message) {
    LogTraceParams logTraceParams = new LogTraceParams();
    logTraceParams.setMessage(message);
    client.logTrace(logTraceParams);
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    return this;
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    TextDocumentItem doc = params.getTextDocument();
    log("did open " + doc.getUri());

    IR ir = new SigCompiler().compile(doc);
    irs.put(doc.getUri(), ir);
    Program program = new BodyCompiler(ir).compile();
    programs.put(doc.getUri(), program);

    List<CompileError> errors = ErrorList.remove(doc.getUri());
    if (errors == null)
      errors = new ArrayList<>();

    List<Diagnostic> diagnostics = errors.stream().map(e -> e.toDiagnostic()).collect(Collectors.toList());
    PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams();
    publishDiagnosticsParams.setUri(doc.getUri());
    publishDiagnosticsParams.setDiagnostics(diagnostics);
    client.publishDiagnostics(publishDiagnosticsParams);

    log("diagnostics published");
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {
  }

  @Override
  public void didSave(DidSaveTextDocumentParams params) {
  }

  @Override
  public void didClose(DidCloseTextDocumentParams params) {
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return this;
  }

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams params) {
  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
  }

  @Override
  public void connect(LanguageClient client) {
    this.client = client;
  }
}
