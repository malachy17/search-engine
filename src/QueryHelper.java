import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class QueryHelper {

	private final TreeMap<String, ArrayList<SearchResult>> map;
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
		String[] words = line.split("\\s+");
		Arrays.sort(words);
		line = String.join(" ", words);
		return line;
	}

	public void toJSON(Path output) throws IOException {
		JSONWriter.writeSearchResults(output, map);
	}
}
