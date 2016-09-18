import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {//

    public static void main(String[] args) throws IOException {
    	
    	Path path = Paths.get(args[0]);
    	Path outFile = Paths.get(args[1]);
    	
    	// Create the inverted index data structure.
    	InvertedIndex index = new InvertedIndex();
    	
    	// Fill the inverted index data structure.
    	DirectoryStreamer streamer = new DirectoryStreamer(path, index);
    	streamer.startTraversing();
    	
    	// Write out the inverted index data structure in JSON.
    	JSONWriter writer = new JSONWriter(outFile, index);
    	writer.startWriting();
    	
    }
}