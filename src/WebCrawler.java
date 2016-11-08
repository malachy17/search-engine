import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class WebCrawler {

	private final InvertedIndex index;

	public WebCrawler(InvertedIndex index) {
		this.index = index;
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
	 * @throws MalformedURLException
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void breadthFirstSearch(String root) throws UnknownHostException, MalformedURLException, IOException {
		Queue<String> queue = new LinkedList<>();
		HashSet<String> set = new HashSet<>();

		set.add(root);
		queue.add(root);

		String seed;
		ArrayList<String> links;

		while (!queue.isEmpty() && set.size() != 50) {
			seed = queue.remove();
			addToIndex(seed);
			links = LinkParser.listLinks(seed);

			for (String link : links) {
				if (!set.contains(link) && set.size() != 50) {
					set.add(link);
					queue.add(link);
				}
			}
		}
	}

	/**
	 * Parses the HTML from a URL in a given queue and gets every word in each
	 * line. Each legal word is then added to the given index.
	 * 
	 * @param index
	 *            the InvertedIndex data structure that will add each word.
	 */
	private void addToIndex(String link) {
		String[] words = HTMLCleaner.fetchWords(link);
		int position = 1;

		for (String word : words) {
			index.add(word, link, position);
			position++;
		}
	}
}
