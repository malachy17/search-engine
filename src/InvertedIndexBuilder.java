import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Traverses a given directory and goes through every text file, line by line
 * and sends every word, the file it's found in, and it's position to the given
 * index for adding.
 */
public class InvertedIndexBuilder {

	/**
	 * Traverses a given directory and goes through every file. If the file ends
	 * with ".txt", then it hands off that file and the index to the parseFile
	 * method.
	 * 
	 * @param path
	 *            the direcotry to start traversing from
	 * @param index
	 *            the inverted index to hand off to the parseFile method.
	 * @throws IOException
	 */
	public static void traverse(Path path, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					InvertedIndexBuilder.traverse(file, index);
				} else {
					if (file.getFileName().toString().toLowerCase().endsWith(".txt")) {
						InvertedIndexBuilder.parseFile(file, index);
					}
				}
			}
		}
	}

	/**
	 * Parses a given text file and the words in each line. Each legal word is
	 * added to the given index.
	 * 
	 * @param input
	 *            the file being parsed
	 * @param index
	 *            the InvertedIndex data structure that will add in each word.
	 * @throws IOException
	 */
	private static void parseFile(Path input, InvertedIndex index) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));) {
			String line = null;
			int position = 1;

			String location = input.normalize().toString();

			while ((line = reader.readLine()) != null) {
				line = InvertedIndexBuilder.clean(line);
				String[] words = line.split("\\s+");

				for (String word : words) {
					if (!word.isEmpty()) {
						index.add(word.trim(), location, position);
						position++;
					}
				}
			}
		}
	}

	/**
	 * For a given line: trims leading and trailing whitespace, converts all
	 * letters to lower-case, and replaces all illegal chars such as punctuation
	 * with empty strings.
	 * 
	 * @param line
	 *            the line being cleaned.
	 * @return the cleaned line.
	 */
	private static String clean(String line) {
		line = line.trim();
		line = line.toLowerCase();
		line = line.replaceAll("\\p{Punct}+", "");
		return line;
	}

	/**
	 * Parses the HTML from a URL in a given queue and gets every word in each
	 * line. Each legal word is then added to the given index.
	 * 
	 * @param queue
	 *            a LinkedList of URLs as String objects.
	 * @param index
	 *            the InvertedIndex data structure that will add each word.
	 */
	public static void parseHTML(LinkedList<String> queue, InvertedIndex index) {
		int position;

		for (String link : queue) {
			String[] words = HTMLCleaner.fetchWords(link);
			position = 1;

			for (String word : words) {
				index.add(word, link, position);
				position++;
			}
		}
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
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static LinkedList<String> breadthFirstSearch(String root)
			throws URISyntaxException, UnknownHostException, IOException {
		LinkedList<String> queue = new LinkedList<>();
		queue.add(root);

		String base, html;
		ArrayList<String> links;
		int pointer = 0;

		while (queue.size() < 50 && queueHasNext(queue, pointer)) {
			base = getBase(queue.get(pointer));
			html = HTTPFetcher.fetchHTML(queue.get(pointer));
			links = LinkParser.listLinks(html);

			for (String link : links) {
				if (queue.size() >= 50)
					break;

				if (!(link.startsWith("#") || link.startsWith("mailto:") || link.startsWith(".."))) {
					link = refine(base, link);

					if (!queue.contains(link)) {
						queue.add(link);
					}
				}
			}

			pointer++;
		}

		return queue;
	}

	/**
	 * Checks if the given LinkedList contains a String at the given location.
	 * 
	 * @param queue
	 *            a LinkedList of Strings.
	 * @param location
	 *            the location in the LinkedList that will be checked for an
	 *            existing String.
	 * @return true if LinkedList contains a String at the given position, false
	 *         if it does not.
	 */
	private static boolean queueHasNext(LinkedList<String> queue, int location) {
		try {
			queue.get(location);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the base of the given urlString by getting it's parent and
	 * removing the ".html" or ".htm" tag.
	 * 
	 * @param stringUrl
	 *            the URL in String format, whose base will be returned.
	 * @return the base of the given URL.
	 * @throws URISyntaxException
	 */
	private static String getBase(String stringUrl) throws URISyntaxException {
		URI uri = new URI(stringUrl);
		URI uriBase = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");

		String base = uriBase.toString();
		base = base.replace(".html", "");
		base = base.replace(".htm", "");

		return base;
	}

	/**
	 * Properly encodes URLs, by using URI. Changes relative URLs to absolute
	 * URLs. Removes fragments off the ends of URLs.
	 * 
	 * @param base
	 *            the URL base (parent) for the given URL String.
	 * @param stringUrl
	 *            the URL in String format that gets refined.
	 * 
	 * @return the properly encoded and absolute version of the given URL with
	 *         fragments removed.
	 * 
	 * @throws URISyntaxException
	 */
	private static String refine(String base, String stringUrl) throws URISyntaxException {
		stringUrl = stringUrl.replaceAll("#.*", "");
		URI uriString = new URI(stringUrl);

		if (uriString.isAbsolute()) {
			return uriString.toString();
		}

		return base + stringUrl;
	}
}
