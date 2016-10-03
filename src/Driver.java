import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	public static void main(String[] args) throws IOException {
		
		// Parses the arguments.
		ArgumentParser argsParser = new ArgumentParser(args);

		if (argsParser.hasFlag("-dir") && argsParser.hasValue("-dir")) {
			Path path = Paths.get(argsParser.getValue("-dir"));

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
			System.err.println("Invalid directory.");
		}
	}
}