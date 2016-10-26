import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * A class that contains both a map of queries to SearchResult objects and an
 * index that is used as the database to search the queries in. It also contains
 * a parseQuery method to parse the given query, find all valid locations in the
 * InvertedIndex and fill up the map with queries and their respective list of
 * SearchResult objects.
 */
public class QueryHelper {

	// A map to store query searches to a list of SearchResult objects.
	private final TreeMap<String, ArrayList<SearchResult>> map;
	// The inverted index of all words found in all files.
	private final InvertedIndex index;

	public QueryHelper(InvertedIndex index) {
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
				line = QueryHelper.clean(line);
				line = QueryHelper.rearrange(line);

				String[] lineArray = line.split(" ");

				if (exact == true) {
					map.put(line, index.exactSearch(lineArray));
				} else {
					map.put(line, index.partialSearch(lineArray));
				}
			}
		}
	}

	/**
	 * Cleans the given String by trimming, making lower case, and removing
	 * punctuation.
	 * 
	 * @param line
	 *            the String to be cleaned.
	 * @return the cleaned String.
	 */
	private static String clean(String line) {
		line = line.trim();
		line = line.toLowerCase();
		line = line.replaceAll("\\p{Punct}+", "");
		return line;
	}

	/**
	 * Rearranges the words in the given String alphabetically.
	 * 
	 * @param line
	 *            the String to be rearranged alphabetically.
	 * @return the cleaned String.
	 */
	private static String rearrange(String line) {
		String[] words = line.split("\\s+");
		Arrays.sort(words);
		line = String.join(" ", words);
		return line;
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
		JSONWriter.writeSearchResults(output, map);
	}
}
