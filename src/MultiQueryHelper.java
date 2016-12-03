import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that contains both a map of queries to SearchResult objects and an
 * index that is used as the database to search the queries in. It also contains
 * a parseQuery method to parse the given query, find all valid locations in the
 * InvertedIndex and fill up the map with queries and their respective list of
 * SearchResult objects.
 */
public class MultiQueryHelper {

	private static final Logger logger = LogManager.getLogger();
	private final ReadWriteLock lock;

	private final WorkQueue minions;
	private int pending;

	// A map to store query searches to a list of SearchResult objects.
	private final TreeMap<String, ArrayList<SearchResult>> map;

	// The inverted index of all words found in all files.
	private final InvertedIndex index;

	public MultiQueryHelper(InvertedIndex index) {
		this.lock = new ReadWriteLock();
		this.minions = new WorkQueue();
		this.pending = 0;

		this.index = index;
		map = new TreeMap<>();
	}

	/**
	 * Goes through each query line, cleans, rearranges the words. Sends those
	 * cleaned words to the exactSearch method. Gets the list from exactSearch,
	 * puts it in a map with the query as the key. Returns the map.
	 * 
	 * @param file
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public void parseQuery(Path file, boolean exact) throws IOException {

		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {
			while ((line = reader.readLine()) != null) {
				minions.execute(new Minion(line, exact));
			}
		}
	}

	/**
	 * Sends a path and a map of queries to be printed in JSON by the JSONWriter
	 * class.
	 * 
	 * @param output
	 *            the location that the map in JSON will be printed to.
	 * @throws IOException
	 */
	public void toJSON(Path output) throws IOException {
		lock.lockReadWrite();
		JSONWriter.writeSearchResults(output, map);
		lock.unlockReadWrite();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link Minion} is created to handle that subdirectory.
	 */
	private class Minion implements Runnable {

		private boolean exact;
		private String line;
		private String[] words;

		public Minion(String line, boolean exact) {
			logger.debug("Minion created for {}", line);
			this.line = line;
			this.exact = exact;

			incrementPending();
		}

		@Override
		public void run() {
			try {
				// Put these in minion instead of parseQuery, because we want
				// each minion to do the work.
				line = InvertedIndexBuilder.clean(line);
				words = line.split("\\s+");
				Arrays.sort(words);
				line = String.join(" ", words);

				if (exact == true) {
					// Efficiency issue fixed where search was inside put()
					// inside lock.
					ArrayList<SearchResult> current = index.exactSearch(words);
					lock.lockReadWrite();
					map.put(line, current);
					lock.unlockReadWrite();

				} else {
					ArrayList<SearchResult> current = index.partialSearch(words);
					lock.lockReadWrite();
					map.put(line, current);
					lock.unlockReadWrite();
				}

				decrementPending();
			} catch (Exception e) {
				logger.warn("Unable to parse {}", line);
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Minion finished {}", line);
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