import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates and stores an inverted index in the form of TreeMap<String,
 * TreeMap<String, TreeSet<Integer>>>. The inverted index documents words and
 * the files that they are found in, as well as the position that specific word
 * can be found in said file.
 */
public class MultiInvertedIndex extends InvertedIndex {

	private static final Logger logger = LogManager.getLogger();
	// private ReadWriteLock lock = new ReadWriteLock();

	/**
	 * The constructor. Instantiates a new index.
	 */
	public MultiInvertedIndex() {
		super();
	}

	/**
	 * Adds a word, it's file, and it's position to the index, after checking to
	 * make sure the word, file, or index is not already included. If the word
	 * is included and not current file, or the word and file but not the
	 * current position, then it will add said file or position accordingly.
	 * 
	 * @param word
	 *            the word to add to the index
	 * @param file
	 *            the file that the word is found in
	 * @param position
	 *            the position in the file where the word is found
	 */
	@Override
	public synchronized void add(String word, String file, Integer position) {
		logger.trace("add(): Adding word \"{}\", file \"{}\", and position \"{}\".", word, file, position);
		super.add(word, file, position);
	}

	/**
	 * Passes the index and a file for the JSONWriter class to use in order to
	 * print the index's data onto a file in JSON format.
	 * 
	 * @param output
	 *            the file that the JSONWriter will print index's data onto.
	 * @throws IOExceptions
	 */
	@Override
	public synchronized void toJSON(Path output) throws IOException {
		logger.debug("toJSON(): Sending {} to be written by the JSONWriter class.", output);
		super.toJSON(output);
	}

	/**
	 * Searches index for the exact word or words in the query, and puts that
	 * word's location in terms of file, count, and size into a SearchResult
	 * object, which is placed into an ArrayList and then returned.
	 * 
	 * @param query
	 *            an array of search queries or a query.
	 * @return a list of SearchResult objects.
	 */
	@Override
	public synchronized ArrayList<SearchResult> exactSearch(String[] query) {
		logger.debug("exactSearch(): Searching for {}.", Arrays.toString(query));
		return super.exactSearch(query);
	}

	/**
	 * Searches index for a word or words that start with the prefix or prefixes
	 * given in the query, and puts those word's or words' location in terms of
	 * file, count, and size into a SearchResult object, which is placed into an
	 * ArrayList and then returned.
	 * 
	 * @param query
	 *            an array of search queries or a query.
	 * @return a list of SearchResult objects.
	 */
	@Override
	public synchronized ArrayList<SearchResult> partialSearch(String[] query) {
		logger.debug("partialSearch(): Searching for {}.", Arrays.toString(query));
		return super.partialSearch(query);
	}
}