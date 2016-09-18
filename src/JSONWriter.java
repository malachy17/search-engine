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
	
	public JSONWriter(Path outFile, InvertedIndex index) {
		
	}
    
    public static final char TAB = '\t';
    public static final char END = '\n';
    
    public static String quote(String text) {
        return String.format("\"%s\"", text);
    }
    
    public static String tab(int n) {
        char[] tabs = new char[n];
        Arrays.fill(tabs, TAB);
        return String.valueOf(tabs);
    }
    
    public static boolean writeArray(Path path, TreeSet<Integer> elements) throws IOException {
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));) {
            writer.write("[" + END);
            
            int count = 1;
            int size = elements.size();
            
            for (Integer element : elements) {
                if (count == size) {
                    writer.write(TAB + element.toString() + END);
                }
                else {
                    writer.write(TAB + element.toString() + "," + END);
                }
                count++;
            }
            
            writer.write("]" + END);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static boolean writeObject(Path path, TreeMap<String, Integer> elements) {
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));) {
            writer.write("{" + END);
            
            int count = 1;
            int size = elements.size();
            
            for (String key : elements.keySet()) {
                if (count == size) {
                    writer.write(TAB + SimpleJSONWriter.quote(key) + ": " + elements.get(key).toString() + END);
                }
                else {
                    writer.write(TAB + SimpleJSONWriter.quote(key) + ": " + elements.get(key).toString() + "," + END);
                }
                count++;
            }
            
            writer.write("}" + END);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static boolean writeNestedObject(Path path, TreeMap<String, TreeSet<Integer>> elements) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));) {
            writer.write("{" + END);
            
            int count = 1;
            int size = elements.size();
            
            for (String key : elements.keySet()) {
                writer.write(TAB + SimpleJSONWriter.quote(key) + ": [" + END);
                
                int count2 = 1;
                int size2 = elements.get(key).size();
                
                for (Integer num : elements.get(key)) {
                    if (count2 == size2) {
                        writer.write(TAB);
                        writer.write(TAB);
                        writer.write(num.toString());
                        writer.write(END);
                    }
                    else {
                        writer.write(TAB);
                        writer.write(TAB);
                        writer.write(num.toString());
                        writer.write(",");
                        writer.write(END);
                    }
                    count2++;
                }
                
                if (count == size) {
                    writer.write(TAB + "]" + END);
                }
                else {
                    writer.write(TAB + "]," + END);
                }
                
                count++;
            }
            
            writer.write("}" + END);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}