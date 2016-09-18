import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

    public static void main(String[] args) {
    	
    	Path path = Paths.get(args[0]);
    	Path outFile = Paths.get(args[1]);
    	
    	InvertedIndex index = new InvertedIndex();
    	DirectoryStreamer streamer = new DirectoryStreamer(path, index);
    	JSONWriter writer(outFile, index);
    	
    }
}