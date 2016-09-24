import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	// TODO main should never throw exceptions, handle them here
	public static void main(String[] args) throws IOException {

		ArgumentParser argsParser = new ArgumentParser(args);

		try {
			// TODO Check if there is a -dir flag first, only build if there is one
			Path path = Paths.get(argsParser.getValue("-dir"));

			// Create the inverted index data structure.
			InvertedIndex index = new InvertedIndex();

			// Fill the inverted index data structure.
			InvertedIndexBuilder streamer = new InvertedIndexBuilder(path, index);
			streamer.startTraversing();
			
			// TODO InvertedIndexBuilder.startTraversing(path, index);

			// TODO only write to JSON if the -index flag is present
			// Write out index onto a JSON file. if (argsParser.hasFlag(-index))
			Path outFile = Paths.get(argsParser.getValue("-index", "index.json"));
			JSONWriter writer = new JSONWriter(outFile, index);
			writer.startWriting();
		}
		catch (NullPointerException e) {
			System.err.println("You must enter a directory!");
		} 	
	}
}