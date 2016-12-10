import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi

@SuppressWarnings("serial")
public class SearchEngineServlet extends HttpServlet {
	private static final String TITLE = "Messages";
	private static Logger log = Log.getRootLogger();

	private ConcurrentLinkedQueue<String> results;

	public SearchEngineServlet() {
		super();
		results = new ConcurrentLinkedQueue<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");

		out.printf("<h1>Google 2.0</h1>%n%n");

		// Keep in mind multiple threads may access at once
		for (String message : results) {
			out.printf("<p>%s</p>%n%n", message);
		}

		printForm(request, response);

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String query = request.getParameter("query");
		query = query == null ? "?" : query;
		query = StringEscapeUtils.escapeHtml4(query);

		results.add(query);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}