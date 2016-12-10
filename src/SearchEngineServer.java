import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchEngineServer {

	public final int port;
	private final InvertedIndex index;

	public SearchEngineServer(int port, InvertedIndex index) {
		this.port = port;
		this.index = index;
	}

	public void startUp() throws Exception {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/");

		server.setHandler(handler);
		server.start();
		server.join();
	}

	@SuppressWarnings("serial")
	private class SearchEngineServlet extends HttpServlet {
		private static final String TITLE = "Messages";

		// private ConcurrentLinkedQueue<String> results;
		private ArrayList<SearchResult> results;

		public SearchEngineServlet() {
			super();
			// results = new ConcurrentLinkedQueue<>();
			results = new ArrayList<>();
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = response.getWriter();
			out.printf("<html>%n%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			out.printf("<h1>Google 2.0</h1>%n%n");

			printForm(request, response);

			// Keep in mind multiple threads may access at once
			for (SearchResult result : results) {
				out.printf("<p><a href=\"%s\">%s</a></p>%n%n", result.getPath(), result.getPath());
				out.printf("<p></p>");
			}

			out.printf("%n</body>%n");
			out.printf("</html>%n");

			response.setStatus(HttpServletResponse.SC_OK);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			String query = request.getParameter("query");
			query = query == null ? "?" : query;
			query = StringEscapeUtils.escapeHtml4(query);

			InvertedIndexBuilderInterface.clean(query);
			String[] words = query.split("\\s+");
			Arrays.sort(words);

			results = index.partialSearch(words);

			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect(request.getServletPath());
		}

		private void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

			PrintWriter out = response.getWriter();
			out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			out.printf("<p><input type=\"text\" name=\"query\" size=\"60\" maxlength=\"100\"/></p>%n");
			out.printf("<p><input type=\"submit\" value=\"Search\"></p>\n%n");
			out.printf("</form>%n");

			out.printf("</body>%n");
			out.printf("</html>%n");
		}

		private String getDate() {
			String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
			DateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(new Date());
		}
	}
}