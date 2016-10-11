import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class QueryHelper {

	// Goes through each query line, cleans, rearranges, and returns a List of
	// the readied queries.

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

	// Cleans the query.
	private static String clean(String line) {
		line = line.trim();
		line = line.toLowerCase();
		line = line.replaceAll("\\p{Punct}+", "");
		return line;
	}

	// Rearranges the words in the query alphabetically.
	private static String rearrange(String line) {
		String[] words = line.split(" ");
		if (words.length > 1) {
			Arrays.sort(words);
			line = "";
			for (String word : words) {
				line += word + " ";
			}
			line.trim();
			return line;
		}
		return line;
	}
}
