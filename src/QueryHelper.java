import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

// TODO Class that stores and does stuff

public class QueryHelper {
	

	// TODO
//	private final TreeMap<String, List<SearchResult>> map;
//	private final InvertedIndex index;
//	
//	public QueryHelper(InvertedIndex index) {
//		
//	}

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
	public static TreeMap<String, ArrayList<SearchResult>> parseQueryExact(Path file, InvertedIndex index)
			throws IOException {

		TreeMap<String, ArrayList<SearchResult>> map = new TreeMap<>();
		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {
			while ((line = reader.readLine()) != null) {
				line = QueryHelper.clean(line);
				line = QueryHelper.rearrange(line);
				map.put(line, index.exactSearch(line));
			}
		}

		return map;
	}
	
	/* TODO
	public void parseQuery(Path file, InvertedIndex index, boolean exact) {
		
	}*/

	/**
	 * Goes through each query line, cleans, rearranges the words. Sends those
	 * cleaned words to the partialSearch method. Gets the list from
	 * exactSearch, puts it in a map with the query as the key. Returns the map.
	 * 
	 * @param file
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public static TreeMap<String, ArrayList<SearchResult>> parseQueryPartial(Path file, InvertedIndex index)
			throws IOException {

		TreeMap<String, ArrayList<SearchResult>> map = new TreeMap<>();
		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {
			while ((line = reader.readLine()) != null) {
				line = QueryHelper.clean(line);
				line = QueryHelper.rearrange(line);
				map.put(line, index.partialSearch(line));
			}
		}

		return map;
	}

	/**
	 * Cleans the query.
	 * 
	 * @param line
	 * @return
	 */
	private static String clean(String line) {
		line = line.trim();
		line = line.toLowerCase();
		line = line.replaceAll("\\p{Punct}+", "");
		return line;
	}

	/**
	 * Rearranges the words in the query alphabetically.
	 * 
	 * @param line
	 * @return
	 */
	private static String rearrange(String line) {
//		String[] words = line.split("\\s+"); // TODO
//		Arrays.sort(words);
//		return String.join(" ", words);
		
		
		
		String[] words = line.split(" ");
		if (words.length > 1) {
			Arrays.sort(words);
			line = "";
			for (String word : words) {
				line += word + " ";
			}
			line = line.trim();
			return line;
		}
		return line;
	}
}
