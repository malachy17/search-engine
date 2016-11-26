import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Crawls links starting from a seed URL in a breadth-first search fashion and
 * sends all words found to an InvertedIndex to be added in.
 */
public class MultiWebCrawler {

	private static final Logger logger = LogManager.getLogger();

	private final InvertedIndex index;
	private final LinkedList<String> queue;
	private final Set<String> urls;

	private final WorkQueue minions;
	private int pending;

	/**
	 * Constructor for the WebCrawler class. Takes in an InvertedIndex which is
	 * used to send words to. Initializes a queue and set of URLs used in
	 * addSeed.
	 * 
	 * @param index
	 *            The InvertedIndex object that words from sendToIndex() will be
	 *            sent to.
	 */
	public MultiWebCrawler(InvertedIndex index, int threads) {
		this.index = index;
		this.queue = new LinkedList<>();
		this.urls = new HashSet<>();

		this.minions = new WorkQueue(threads);
		this.pending = 0;
	}

	/**
	 * Starts at the seed web-page and performs a breadth-first search upon that
	 * web-page's links, and their links and so forth until it fills a the urls
	 * set with 50 links. Uses the set to keep track of the amount of links and
	 * a queue to iterate each scraped link with. Sends each valid link's HTML
	 * to the sendToIndex() method where that link's words will be passed onto
	 * the InvertedIndex for adding.
	 * 
	 * @param seed
	 *            The first web-page crawled, and parent to all other web-pages.
	 * 
	 * @throws UnknownHostException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void addSeed(String seed) throws UnknownHostException, MalformedURLException, IOException {
		urls.add(seed);
		queue.add(seed);

		while (!queue.isEmpty()) {
			String current = queue.remove();
			String html = HTTPFetcher.fetchHTML(current);
			// sendToIndex(html, current);
			minions.execute(new Minion(html, current));
			ArrayList<String> links = LinkParser.listLinks(html, current);

			for (String link : links) {
				if (!urls.contains(link) && urls.size() != 50) {
					urls.add(link);
					queue.add(link);
				}
			}
		}
	}

	/**
	 * Takes in HTML and that html's absolute URL and sends each word's name,
	 * file that it is found in, and position within the file to the
	 * InvertedIndex to be added in.
	 * 
	 * @param html
	 *            The HTML containing the words that this method will grab.
	 * @param link
	 *            The web-page's URL name that the word is found in.
	 */
	private void sendToIndex(String html, String link) {
		logger.debug("sendToIndex(): Sending {} to the index.", link);
		String[] words = HTMLCleaner.fetchHTMLWords(html);
		int position = 1;

		for (String word : words) {
			index.add(word, link, position);
			position++;
		}
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link Minion} is created to handle that subdirectory.
	 */
	private class Minion implements Runnable {

		private String html;
		private String link;

		public Minion(String html, String link) {
			logger.debug("Minion created for {}", link);
			this.html = html;
			this.link = link;

			// Indicate we now have "pending" work to do. This is necessary
			// so we know when our threads are "done", since we can no longer
			// call the join() method on them.
			incrementPending();
		}

		@Override
		public void run() {
			try {
				sendToIndex(html, link);
				// Indicate that we no longer have "pending" work to do.
				decrementPending();
			} catch (Exception e) {
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Minion finished {}", link);
		}
	}

	/**
	 * Indicates that we now have additional "pending" work to wait for. We need
	 * this since we can no longer call join() on the threads. (The threads keep
	 * running forever in the background.)
	 *
	 * We made this a synchronized method in the outer class, since locking on
	 * the "this" object within an inner class does not work.
	 */
	private synchronized void incrementPending() {
		pending++;
		logger.debug("Pending is now {}", pending);
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify any
	 * waiting threads if we no longer have any more pending work left.
	 */
	private synchronized void decrementPending() {
		pending--;
		logger.debug("Pending is now {}", pending);

		if (pending <= 0) {
			this.notifyAll();
		}
	}

	/**
	 * Helper method, that helps a thread wait until all of the current work is
	 * done. This is useful for resetting the counters or shutting down the work
	 * queue.
	 */
	public synchronized void finish() {
		try {
			while (pending > 0) {
				logger.debug("Waiting until finished");
				this.wait();
			}
		} catch (InterruptedException e) {
			logger.debug("Finish interrupted", e);
		}
	}

	/**
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	public synchronized void shutdown() {
		logger.debug("Shutting down");
		finish();
		minions.shutdown();
	}
}
