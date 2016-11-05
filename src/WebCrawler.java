import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

public class WebCrawler {

	private LinkedList<String> queue;

	public WebCrawler() {
		queue = new LinkedList<>();
	}

	/**
	 * Parses the HTML from a URL in a given queue and gets every word in each
	 * line. Each legal word is then added to the given index.
	 * 
	 * @param index
	 *            the InvertedIndex data structure that will add each word.
	 */
	public void parseHTML(InvertedIndex index) {
		int position;

		for (String link : queue) {
			String[] words = HTMLCleaner.fetchWords(link);
			position = 1;

			for (String word : words) {
				index.add(word, link, position);
				position++;
			}
		}
	}

	/**
	 * Searches a given String representing a URL linked to a HTML web-page and
	 * adds it to a LinkedList. Then performs a breadth-first search on this
	 * root URL and adds found URL links to the LinkedList in breadth-first
	 * search order. It stops when there are no more URLs to add, or when the
	 * LinkedList's size reaches 50. Then, the LinkedList is returned.
	 * 
	 * @param root
	 *            the given String which the method starts its breadth-first
	 *            search from.
	 * 
	 * @return A LinkedList of URLs in String format.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void breadthFirstSearch(String root) throws URISyntaxException, UnknownHostException, IOException {

		queue.add(root);

		String base, html;
		ArrayList<String> links;
		int pointer = 0;

		while (queue.size() < 50 && queueHasNext(pointer)) {
			base = getBase(queue.get(pointer));
			html = HTTPFetcher.fetchHTML(queue.get(pointer));
			links = LinkParser.listLinks(html); // TODO Pass in the root url
												// here

			for (String link : links) {
				if (queue.size() >= 50)
					break;

				if (!(link.startsWith("#") || link.startsWith("mailto:") || link.startsWith(".."))) {
					link = refine(base, link);

					if (!queue.contains(link))
						queue.add(link);
				}
			}

			pointer++;
		}
	}

	/**
	 * Checks if the given LinkedList contains a String at the given location.
	 * 
	 * @param location
	 *            the location in the LinkedList that will be checked for an
	 *            existing String.
	 * 
	 * @return true if LinkedList contains a String at the given position, false
	 *         if it does not.
	 */
	private boolean queueHasNext(int location) {
		try {
			queue.get(location);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the base of the given urlString by getting it's parent and
	 * removing the ".html" or ".htm" tag.
	 * 
	 * @param stringUrl
	 *            the URL in String format, whose base will be returned.
	 * @return the base of the given URL.
	 * @throws URISyntaxException
	 */
	private String getBase(String stringUrl) throws URISyntaxException {
		URI uri = new URI(stringUrl);
		URI uriBase = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");

		String base = uriBase.toString();
		base = base.replace(".html", "");
		base = base.replace(".htm", "");

		return base;

		// TODO ?? Shouldn't need...
	}

	/**
	 * Properly encodes URLs, by using URI. Changes relative URLs to absolute
	 * URLs. Removes fragments off the ends of URLs.
	 * 
	 * @param base
	 *            the URL base (parent) for the given URL String.
	 * @param stringUrl
	 *            the URL in String format that gets refined.
	 * 
	 * @return the properly encoded and absolute version of the given URL with
	 *         fragments removed.
	 * 
	 * @throws URISyntaxException
	 */
	private String refine(String base, String stringUrl) throws URISyntaxException {
		stringUrl = stringUrl.replaceAll("#.*", "");
		URI uriString = new URI(stringUrl);

		if (uriString.isAbsolute()) {
			return uriString.toString();
		}

		return base + stringUrl;
	}

}
