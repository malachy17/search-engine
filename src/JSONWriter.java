import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class JSONWriter {
	
    private final char TAB = '\t';
    private final char END = '\n';
	
	private Path outFile;
	private InvertedIndex index;
	
	public JSONWriter(Path outFile, InvertedIndex index) {
		this.outFile = outFile;
		this.index = index;
	}
	
	public void startWriting() {
		writeNestedObject();
	}

    private void writeNestedObject() throws IOException {
        
    	try (BufferedWriter writer = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));) {
            
        	writer.write("{" + END);
        	
        	for (String word : index.key)
            
            
        catch (Exception e) {
            System.out.println("Error in writeNestedObject");
        }
    }
    
    private String addComma() {
    	if {
    		
    	}
    	else {
    		
    	}
    }
    
    private String quote(String text) {
        return String.format("\"%s\"", text);
    }

    private String tab(int n) {
        char[] tabs = new char[n];
        Arrays.fill(tabs, TAB);
        return String.valueOf(tabs);
    }

}