import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO Have to have Javadoc comments for all classes and methods

public class Driver {

	public static void main(String[] args) throws IOException { // TODO Cannot throw Exceptions here
		
		// Parses the arguments.
		ArgumentParser argsParser = new ArgumentParser(args);

		if (argsParser.hasFlag("-dir") && argsParser.hasValue("-dir")) {
			Path path = Paths.get(argsParser.getValue("-dir"));
			
			if (Files.exists(path) && Files.isDirectory(path)) {
				// Create the inverted index data structure.
				InvertedIndex index = new InvertedIndex(); // TODO Do this outside of the if block to make easier for future projects

				// Fill the inverted index data structure.
				InvertedIndexBuilder.startTraversing(path, index);

				// TODO Move this to a separate if block outside of hasFlag(-dir)
				if (argsParser.hasFlag("-index")) {
					Path outFile = Paths.get(argsParser.getValue("-index", "index.json"));
					index.toJSON(outFile);
				}
			}
			else {
				System.err.println("Invalid directory");
			}
		}
		else {
			System.err.println("You must a directory flag and a directory.");
		}

		// TODO Driver logic
//		ArgumentParser parser = new ArgumentParser(args);
//		InvertedIndex index = new InvertedIndex();
//		
//		if(parser.hasFlag(-dir)) {
//			try {
//				stuff for building
//			}
//			catch (your exceptions) {
//				user friendly error message
//			}
//		}
//		
//		if (parser.hasFlag(-index)) {
//			try {
//				write to file
//			}
//			catch (your exceptions) {
//				user friendly error message
//			}
//		}
//		
		
	}
}