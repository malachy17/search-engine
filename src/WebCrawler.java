import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

public class WebCrawler {

	// TODO Make the InvertedIndex a parameter to the WebCrawler constructor and an instance member
	
	private LinkedList<String> queue; // TODO Use final where appropriate?
	
	// TODO private final Set<String> urls;

	public WebCrawler() {
		queue = new LinkedList<>();
	}
	
	/*
	public void addSeed(String url) {
		if we haven't already parsed the max number of links and this is a unique link (check set):
			add the link to the queue and to the set
		
		while (queue.hasNext()) {
			String current = queue.remove();
			
			String html = HTTPFetcher.fetchHTML(current);
			parseLinks(current, html) which will get all of the links, for each link decide if it should be added to the queue and set
			parseHTML(html)
		}
	}
	*/

	/**
	 * Parses the HTML from a URL in a given queue and gets every word in each
	 * line. Each legal word is then added to the given index.
	 * 
	 * @param index
	 *            the InvertedIndex data structure that will add each word.
	 */
	public void parseHTML(InvertedIndex index) { // TODO Instead of the InvertedIndex as a parameter, take the HTML that has already been downloaded
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

		ArrayList<String> links;
		int pointer = 0;

		while (queue.size() < 50 && queueHasNext(pointer)) {
			links = LinkParser.listLinks(queue.get(pointer));

			for (String link : links) {
				if (queue.size() >= 50) {
					break;
				}
				if (!queue.contains(link)) {
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
}
