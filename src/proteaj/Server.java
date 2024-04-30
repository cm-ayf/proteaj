package proteaj;

import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

import proteaj.env.type.RootTypeResolver;
import proteaj.error.ErrorList;
import proteaj.error.NotFoundError;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.jsonrpc.Launcher;

public class Server {
  public static void main(String[] argsArray) {
    CommandLineArgs args = CommandLineArgs.parse(argsArray);
    ErrorList.init();

    if (!args.additionalClassPath.isEmpty())
      try {
        RootTypeResolver.getInstance().appendClassPath(args.additionalClassPath);
      } catch (NotFoundError e) {
        System.err.println("invalid class path : " + args.additionalClassPath);
        return;
      }

    LanguageServer server = new ProteajLanguageServer();
    Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in,
        System.out);

    if (server instanceof LanguageClientAware clientAware) {
      LanguageClient client = launcher.getRemoteProxy();
      clientAware.connect(client);
    }
    launcher.startListening();
  }
}
