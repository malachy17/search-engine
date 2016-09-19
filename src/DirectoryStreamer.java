import java.nio.file.DirectoryStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryStreamer {
	
	private Path path;
	private InvertedIndex index;
	
	public DirectoryStreamer(Path path, InvertedIndex index) {
		this.path = path;
		this.index = index;
	}
	
	public void startTraversing() throws IOException {
		traverse(path);
	}
    
    private void traverse(Path directory) throws IOException {
        if (Files.isDirectory(directory)) {
            traverse2(directory);
        }
        else {
        	if (directory.getFileName().toString().toLowerCase().endsWith(".txt")) {
        		parseFile(directory);
        	}
        }
    }
    
    private void traverse2(Path path) throws IOException {
        try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
            for (Path file : listing) {
                if (Files.isDirectory(file)) {
                    traverse2(file);
                }
                else {
                	if (file.getFileName().toString().toLowerCase().endsWith(".txt")) {
                		parseFile(file);
                	}
                }
            }
        }
    }
    
    private void parseFile(Path input) throws IOException {
    	
    	try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));) {
    		String line = null;
    		int position = 1;
    		
    		while ((line = reader.readLine()) != null) {
    			line = clean(line);
    			String[] words = line.split(" ");
    			
    			for (String word : words) {
    				if (!word.equalsIgnoreCase("")) {
    					index.considerAdding(word, (input.getFileName().normalize().toString()), position);
    					position++;
    				}
    			}
    		}
    	}
    }
    
    private String clean(String line) {
    	line = line.trim();
    	line = line.toLowerCase();
    	line = line.replaceAll("\\p{Punct}+", "");
    	return line;
    }
}