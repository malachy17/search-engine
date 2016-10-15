import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Creates and stores an inverted index in the form of TreeMap<String,
 * TreeMap<String, TreeSet<Integer>>>. The inverted index documents words and
 * the files that they are found in, as well as the position that specific word
 * can be found in said file.
 */
public class InvertedIndex {

	/**
	 * The inverted index data structure.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * The constructor. Instantiates a new index.
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
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
	public void add(String word, String file, Integer position) {
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<>());
		}

		if (index.get(word).get(file) == null) {
			index.get(word).put(file, new TreeSet<>());
		}

		index.get(word).get(file).add(position);
	}

	/**
	 * Passes the index and a file for the JSONWriter class to use in order to
	 * print the index's data onto a file in JSON format.
	 * 
	 * @param output
	 *            the file that the JSONWriter will print index's data onto.
	 * @throws IOExceptions
	 */
	public void toJSON(Path output) throws IOException {
		JSONWriter.writeNestedObject(output, index);
	}

	/**
	 * Searches index for the word or words in the query, and puts the necessary
	 * data into a SearchResult object, which is place into an ArrayList and
	 * returned.
	 * 
	 * @param query
	 * @return
	 */
	public ArrayList<SearchResult> exactSearch(String query) {
		// TODO Double check StackOverflow if ArrayList or LinkedList is better for sorting
		ArrayList<SearchResult> list = new ArrayList<>();
		
		// TODO Use a map!
//		Map<String, SearchResult> map = ???

		// Goes through each query.
		String[] words = query.split(" "); // TODO Take already split queries as input (data structures tend not to do string parsing)

		// Goes through each word in this query.
		for (String word : words) {
			if (index.containsKey(word)) {

				for (String file : index.get(word).keySet()) {
					int count = index.get(word).get(file).size();
					int firstPosition = index.get(word).get(file).first();

					// TODO
//					if we have seen this result before (if we have the file as a key in our map)
//						update the already existing result
//					else
//						add the file, and a new result to the map
						
						list.add(new SearchResult(count, firstPosition, file));
				}

			}
		}

		list = merge(list);
		
		// TODO
//		list.addAll(map.values());
		
		
		Collections.sort(list);
		return list;
	}

	/**
	 * Searches index for the words that start with the prefix or prefixes in
	 * the query, and puts the necessary data into a SearchResult object, which
	 * is place into an ArrayList and returned.
	 * 
	 * @param query
	 * @return
	 */
	public ArrayList<SearchResult> partialSearch(String query) {
		ArrayList<SearchResult> list = new ArrayList<>();

		// Goes through each query.
		String[] prefixes = query.split(" ");

		// Goes through each word in this query.
		for (String prefix : prefixes) {

			// Goes through each key in the tailMap of the index.
			for (String indexWord : index.tailMap(prefix).keySet()) {

				if (!indexWord.startsWith(prefix)) {
					break;
				}

				// Goes through each file in this key.
				for (String file : index.get(indexWord).keySet()) {
					int count = index.get(indexWord).get(file).size();
					int firstPosition = index.get(indexWord).get(file).first();

					list.add(new SearchResult(count, firstPosition, file));
				}
			}
		}

		list = merge(list);
		Collections.sort(list);
		return list;
	}

	/**
	 * Finds SearchResult objects in the results ArrayList that share the same
	 * file, and combines their data into one SearchResult object. Adds this
	 * SearchResult object and unaffected SearchResult objects into a new
	 * ArrayList.
	 * 
	 * @param results
	 * @return
	 */
	private ArrayList<SearchResult> merge(ArrayList<SearchResult> results) {
		// The perfected ArrayList to be returned.
		ArrayList<SearchResult> newResults = new ArrayList<>();

		// Store all files found in results.
		HashSet<String> files = new HashSet<String>();
		for (SearchResult result : results) {
			files.add(result.getPath());
		}

		// For each file, get all the words in results that have that
		// file, and combine the data to create 1 SearchResult object
		// per file.
		for (String file : files) {
			int totalCount = 0;
			int firstPosition = -1;

			for (SearchResult result : results) {

				if (result.getPath().equals(file)) {
					totalCount += result.getCount();

					if ((firstPosition == -1) || (result.getFirstPosition() < firstPosition)) {
						firstPosition = result.getFirstPosition();
					}
				}
			}

			newResults.add(new SearchResult(totalCount, firstPosition, file));
		}

		return newResults;
	}
}