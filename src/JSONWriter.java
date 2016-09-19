import java.nio.file.Path;
import java.util.Arrays;
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
	
	public void startWriting() throws IOException {
		writeNestedObject();
	}

    private void writeNestedObject() throws IOException {
        
    	try (BufferedWriter writer = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));) {
        	writer.write("{" + END);
        	
        	int count1 = 1;
        	int size1 = index.getWordSet().size();
        	for (String word : index.getWordSet()) {
        		writer.write(tab(1) + quote(word) + ": {" + END);
        		
        		int count2 = 1;
        		int size2 = index.getFileSet(word).size();
        		for (String file : index.getFileSet(word)) {
        			writer.write(tab(2) + quote(file) + ": [" + END);
        			
        			int count3 = 1;
        			int size3 = index.getIntegerSet(word, file).size();
        			for (Integer position : index.getIntegerSet(word, file)) {
        				writer.write(tab(3) + position + addComma(count3, size3) + END);
        				count3++;
        			}
        			
        			writer.write(tab(2) + "]" + addComma(count2, size2) + END);
        			count2++;
        		}
        		
        		writer.write(tab(1) + "}" + addComma(count1, size1) + END);
        		count1++;
        	}
        	
        	writer.write("}" + END);
    	}   
        catch (Exception e) {
            System.err.println("Error in writeNestedObject");
        }
    }
    
    private String addComma(int count, int size) {
    	if (count < size) {
    		return ",";
    	}
    	else {
    		return "";
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