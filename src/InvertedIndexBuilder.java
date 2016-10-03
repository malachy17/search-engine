import java.nio.file.DirectoryStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class InvertedIndexBuilder {

	public static void startTraversing(Path path, InvertedIndex index) throws IOException {
		InvertedIndexBuilder.traverse(path, index);
	}

	private static void traverse(Path path, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					InvertedIndexBuilder.traverse(file, index);
				}
				else {
					if (file.getFileName().toString().toLowerCase().endsWith(".txt")) {
						InvertedIndexBuilder.parseFile(file, index);
					}
				}
			}
		}
	}

	private static void parseFile(Path input, InvertedIndex index) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));) {
			String line = null;
			int position = 1;

			while ((line = reader.readLine()) != null) {
				line = InvertedIndexBuilder.clean(line);
				String[] words = line.split(" ");

				for (String word : words) {
					if (!word.equalsIgnoreCase("")) {
						index.add(word.trim(), (input.normalize().toString()), position);
						position++;
					}
				}
			}
		}
	}

	private static String clean(String line) {
		line = line.trim();
		line = line.toLowerCase();
		line = line.replaceAll("\\p{Punct}+", "");
		return line;
	}
}