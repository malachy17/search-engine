import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

	public static LinkedList<String> breadthFirstSearch(String root) throws MalformedURLException, URISyntaxException {
		LinkedList<String> queue = new LinkedList<>();
		queue.add(root);

		String base, html;
		ArrayList<String> links;
		int pointer = 0;

		while (queue.size() < 50 && queueHasNext(queue, pointer)) {
			base = getBase(queue.get(pointer));
			html = HTMLCleaner.fetchHTML(queue.get(pointer));
			links = LinkParser.listLinks(html);

			for (String link : links) {

				if (queue.size() >= 50) {
					break;
				}

				if (link.startsWith("#") || link.startsWith("mailto:") || link.startsWith("..")) {
					continue;
				}

				link = refine(base, link);

				if (queue.contains(link)) {
					continue;
				}

				queue.add(link);
			}

			pointer++;

		}

		// System.out.println("queue:"); // TODO delete
		// for (String element : queue) {
		// System.out.println("\t" + element);
		// }

		return queue;
	}

	private static boolean queueHasNext(LinkedList<String> queue, int pointer) {
		try {
			queue.get(pointer);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private static String getBase(String urlString) throws URISyntaxException {
		URI uri = new URI(urlString);
		URI uriBase = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
		String base = uriBase.toString();

		base = base.replace(".htm", "");
		base = base.replace(".html", "");
		return base;
	}

	private static String refine(String base, String urlString) throws URISyntaxException {
		URI uriString = new URI(urlString);
		if (uriString.isAbsolute()) {
			return uriString.toString().replaceAll("#.*", "");
		}
		String newURL = base + urlString;
		newURL = newURL.replaceAll("#.*", "");
		return newURL;
	}
}
