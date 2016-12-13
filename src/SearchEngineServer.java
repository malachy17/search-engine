import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchEngineServer {

	public final int port;
	private final InvertedIndex index;

	private final String googleLogo;
	private final String twoPointZeroLogo;

	private boolean incognito;
	private boolean exact;

	public SearchEngineServer(int port, InvertedIndex index) {
		this.port = port;
		this.index = index;

		this.googleLogo = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
		this.twoPointZeroLogo = "https://smashingboxes.com/media/W1siZiIsIjIwMTUvMTAvMjAvMTAvNDEvNDgvOTE5L2FuZ3VsYXJfMi4wLnBuZyJdXQ/angular%202.0.png?sha=c182c65bfad4aa24";

		this.incognito = false;
		this.exact = false;
	}

	public void startUp() throws Exception {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/");
		handler.addServletWithMapping(new ServletHolder(new SearchHistoryServlet()), "/searchHistory");
		handler.addServletWithMapping(new ServletHolder(new VisitedResultsServlet()), "/visitedResults");

		server.setHandler(handler);
		server.start();
		server.join();
	}

	@SuppressWarnings("serial")
	private class SearchEngineServlet extends CookieBaseServlet {
		private static final String TITLE = "Google 2.0";
		private ArrayList<SearchResult> results;

		public SearchEngineServlet() {
			super();
			results = new ArrayList<>();
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			printForm(request, response);
			PrintWriter out = response.getWriter();

			if (request.getParameter("dontTrack") != null) {
				incognito = incognito == false ? true : false;
			}
			out.printf("<p><font color = \"white\">Incognito Mode: %s</font></p>", incognito);

			if (request.getParameter("exact") != null) {
				exact = exact == false ? true : false;
			}
			out.printf("<p><font color = \"white\">Exact Search: %s</font></p>", exact);

			if (request.getParameter("query") != null) {
				String query = request.getParameter("query");
				query = StringEscapeUtils.escapeHtml4(query);

				InvertedIndexBuilderInterface.clean(query);
				String[] words = query.split("\\s+");
				Arrays.sort(words);

				if (exact == true) {
					results = index.exactSearch(words);
				} else {
					results = index.partialSearch(words);
				}

				out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
				for (SearchResult result : results) {
					out.printf("<p><center><input type=\"submit\" name=\"url\" value=\"%s\"></center></p>\n%n",
							result.getPath());
				}
				out.printf("</form>%n");

				if (incognito == false) {
					makeCookie(request, response, query, SearchHistoryServlet.COOKIE_NAME);
				}
			}

			out.printf("%n</body>%n");
			out.printf("</html>%n");

			response.setStatus(HttpServletResponse.SC_OK);
		}

		private void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

			PrintWriter out = response.getWriter();
			out.printf("<html>%n%n");
			out.printf("<head>");
			out.printf("<title>%s</title>%n", TITLE);
			out.printf("<style> body { background-color:#000000; } </style>");
			out.printf("</head>");
			out.printf("<body>%n");

			out.printf("<h1><center><img src=\"%s\"/>%n", googleLogo);
			out.printf("<img src=\"%s\" height=\"128\" width=\"128\" />%n</center></h1>", twoPointZeroLogo);

			out.printf("<form method=\"get\" action=\"%s\">%n", request.getServletPath());
			out.printf("<p><center><input type=\"text\" name=\"query\" size=\"60\" maxlength=\"100\"/></center></p>%n");
			out.printf("<p><center><input type=\"submit\" value=\"Search\"></center></p>\n%n");
			out.printf("</form>%n");

			out.printf("<form method=\"get\" action=\"%s\">%n", "searchHistory");
			out.printf("\t<input type=\"submit\" value=\"Search History\">%n");
			out.printf("</form>%n");

			out.printf("<form method=\"get\" action=\"%s\">%n", "visitedResults");
			out.printf("\t<input type=\"submit\" value=\"Visited Results\">%n");
			out.printf("</form>%n");

			out.printf("<form method=\"get\" action=\"%s\">%n", request.getRequestURI());
			out.printf("\t<input type=\"submit\" name=\"dontTrack\" value=\"Go Incognito\" >%n");
			out.printf("</form>%n");

			out.printf("<form method=\"get\" action=\"%s\">%n", request.getRequestURI());
			out.printf("\t<input type=\"submit\" name=\"exact\" value=\"Exact/Partial\">%n");
			out.printf("</form>%n");

			out.printf("</body>%n");
			out.printf("</html>%n");
		}

		private void makeCookie(HttpServletRequest request, HttpServletResponse response, String query, String name) {
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(name)) {
						String oldValue = cookie.getValue();
						String newValue = query + " : " + getShortDate();
						cookie.setValue(oldValue + "_" + newValue);
						response.addCookie(cookie);
						return;
					}
				}
			}
			response.addCookie(new Cookie(name, query + " : " + getShortDate()));
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			String url = request.getParameter("url");
			url = StringEscapeUtils.escapeHtml4(url);

			if (request.getParameter("dontTrack") != null) {
				incognito = incognito == false ? true : false;
			}

			if (incognito == false) {
				makeCookie(request, response, url, VisitedResultsServlet.COOKIE_NAME);
			}

			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect(url);
		}
	}
}