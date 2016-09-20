import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {
 
    public static void main(String[] args) throws IOException {
    	
    	ArgumentParser argsParser = new ArgumentParser(args);
    	
    	try {
    		Path path = Paths.get(argsParser.getValue("-dir"));
    	
    		// Create the inverted index data structure.
    		InvertedIndex index = new InvertedIndex();
    	
    		// Fill the inverted index data structure.
    		DirectoryStreamer streamer = new DirectoryStreamer(path, index);
    		streamer.startTraversing();
    	
    		// Write out index onto a JSON file.
    		Path outFile = Paths.get(argsParser.getValue("-index", "index.json"));
    		JSONWriter writer = new JSONWriter(outFile, index);
    		writer.startWriting();
    	}
    	catch (NullPointerException e) {
    		System.err.println("You must enter a directory!");
    	}
    	
    }
}