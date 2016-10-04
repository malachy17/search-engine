import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO Have to have Javadoc comments for all classes and methods

public class Driver {

	public static void main(String[] args) {
		
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex();
		
		if(parser.hasFlag("-dir")) {
			try {
				Path path = Paths.get(parser.getValue("-dir"));
				InvertedIndexBuilder.startTraversing(path, index);
			}
			catch (IOException e) {
				System.err.println("You got an IOException.");
			}
			catch (NullPointerException e) {
				System.err.println("Enter a directory after the \"dir\" flag.");
			}
		}
		
		if (parser.hasFlag("-index")) {
			try {
				Path outFile = Paths.get(parser.getValue("-index", "index.json"));
				index.toJSON(outFile);
			}
			catch (Exception e) {
				System.err.println("You got an IOException.");
			}
		}
		
		
	}
}