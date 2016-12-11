import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HistoryServlet extends HttpServlet {
	private static final String TITLE = "History";
	private HashMap<String, String> searched;
	private ReadWriteLock lock;

	public HistoryServlet() {
		super();
		searched = new HashMap<>();
		lock = new ReadWriteLock();
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

		out.printf("<h1>History</h1>%n%n");

		// Keep in mind multiple threads may access at once
		for (String result : searched.keySet()) {
			lock.lockReadOnly();
			out.printf(String.format("%s<br><font size=\"-2\">[ posted at %s ]</font>", result, searched.get(result)));
			lock.unlockReadOnly();
		}

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	public void add(String result) {
		String date = getDate();
		lock.lockReadWrite();
		searched.put(result, date);
		lock.unlockReadWrite();
	}

	public void clear() {
		lock.lockReadWrite();
		searched.clear();
		lock.unlockReadWrite();
	}

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}