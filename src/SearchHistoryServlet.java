import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Demonstrates how to create, use, and clear cookies. Vulnerable to attack
 * since cookie values are not sanitized prior to use!
 *
 * @see CookieBaseServlet
 * @see SearchHistoryServlet
 * @see CookieConfigServlet
 */
@SuppressWarnings("serial")
public class SearchHistoryServlet extends CookieBaseServlet {

	public static final String VISIT_DATE = "Visited";
	public static final String VISIT_COUNT = "Count";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("GET " + request.getRequestURL().toString());

		if (request.getRequestURI().endsWith("favicon.ico")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		prepareResponse("History", response);

		PrintWriter out = response.getWriter();
		out.printf("<p>To clear saved cookies, please press \"Clear\".</p>%n");
		out.printf("%n");
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getRequestURI());
		out.printf("\t<input type=\"submit\" value=\"Clear\">%n");
		out.printf("</form>%n");

		Map<String, String> cookies = getCookieMap(request);
		for (String cookie : cookies.keySet()) {
			out.printf("<p>%s     |     %s</p>", cookie, cookies.get(cookie));
		}

		finishResponse(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("POST " + request.getRequestURL().toString());

		clearCookies(request, response);

		prepareResponse("Configure", response);

		PrintWriter out = response.getWriter();
		out.printf("<p>Your cookies for this site have been cleared.</p>%n%n");

		finishResponse(request, response);
	}
}