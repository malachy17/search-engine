import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses links from HTML. Assumes the HTML is valid, and all attributes are
 * properly quoted and URL encoded.
 *
 * <p>
 * See the following link for details on the HTML Anchor tag:
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a"> https:
 * //developer.mozilla.org/en-US/docs/Web/HTML/Element/a </a>
 * 
 * @see LinkTester
 */
public class LinkParser {

	/**
	 * The regular expression used to parse the HTML for links.
	 */
	public static final String REGEX = "(?i)<a([^>]*?)(href)=\"(.+?)\"";
	// TODO Remove extra groups and update number below

	/**
	 * The group in the regular expression that captures the raw link.
	 */
	public static final int GROUP = 3; // TODO Change if necessary

	/**
	 * Parses the provided text for HTML links.
	 *
	 * @param url
	 *            the url whose text will be searched for links.
	 * @return list of URLs found in HTML code
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws UnknownHostException
	 */
	public static ArrayList<String> listLinks(String url)
			throws UnknownHostException, MalformedURLException, IOException {
		String text = HTTPFetcher.fetchHTML(url); // TODO Downloading webpage twice, pass in the url and the already downloaded HTML?
		// System.out.println(text);

		// list to store links
		ArrayList<String> links = new ArrayList<String>();

		// compile string into regular expression
		Pattern p = Pattern.compile(REGEX);

		// match provided text against regular expression
		Matcher m = p.matcher(text.replaceAll("\\s", "")); // TODO Do not replace?

		// TODO Discouraged code style
		String strLink;
		URL link, absolute, base = new URL(url);

		// loop through every match found in text
		while (m.find()) {

			// Get this URL.
			strLink = m.group(GROUP); // TODO Could declare within here

			// Convert the URL to an abolute URL.
			absolute = new URL(base, strLink);

			// Remove the URL's fragments.
			link = new URL(absolute.getProtocol(), absolute.getHost(), absolute.getFile());

			// Convert the URL to a String.
			strLink = link.toString();

			// add the appropriate group from regular expression to list
			links.add(strLink);
		}

		return links;
	}
}