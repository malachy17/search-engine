import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class WebCrawler {

	private final InvertedIndex index;
	private final LinkedList<String> queue;
	private final Set<String> urls;

	public WebCrawler(InvertedIndex index) {
		this.index = index;
		this.queue = new LinkedList<>();
		this.urls = new HashSet<>();
	}

	/*
	 * public void addSeed(String url) { if we haven't already parsed the max
	 * number of links and this is a unique link (check set): add the link to
	 * the queue and to the set
	 * 
	 * while (queue.hasNext()) { String current = queue.remove();
	 * 
	 * String html = HTTPFetcher.fetchHTML(current); parseLinks(current, html)
	 * which will get all of the links, for each link decide if it should be
	 * added to the queue and set parseHTML(html) } }
	 */
	public void addSeed(String seed) throws UnknownHostException, MalformedURLException, IOException {
		urls.add(seed);
		queue.add(seed);

		while (!queue.isEmpty()) {
			String current = queue.remove();
			String html = HTTPFetcher.fetchHTML(current);
			sendToIndex(html, current);
			ArrayList<String> links = LinkParser.listLinks(html, current);

			for (String link : links) {
				if (!urls.contains(link) && urls.size() != 50) {
					urls.add(link);
					queue.add(link);
				}
			}
		}
	}

	private void sendToIndex(String html, String link) {
		String[] words = HTMLCleaner.fetchWords(html);
		int position = 1;

		for (String word : words) {
			index.add(word, link, position);
			position++;
		}
	}
}
