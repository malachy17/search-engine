import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchEngineServer {

	public static void startUp(int port, InvertedIndex index) throws Exception {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/");

		server.setHandler(handler);
		server.start();
		server.join();
	}
}