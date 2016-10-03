import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	public static void main(String[] args) throws IOException {
		
		// Parses the arguments.
		ArgumentParser argsParser = new ArgumentParser(args);

		if (argsParser.hasFlag("-dir") && argsParser.hasValue("-dir")) {
			Path path = Paths.get(argsParser.getValue("-dir"));
			
			if (Files.exists(path) && Files.isDirectory(path)) {
				// Create the inverted index data structure.
				InvertedIndex index = new InvertedIndex();

				// Fill the inverted index data structure.
				InvertedIndexBuilder.startTraversing(path, index);

				if (argsParser.hasFlag("-index")) {
					Path outFile = Paths.get(argsParser.getValue("-index", "index.json"));
					JSONWriter.startWriting(outFile, index);
				}
			}
			else {
				System.err.println("Invalid directory");
			}
		}
		else {
			System.err.println("You must a directory flag and a directory.");
		}
	}
}