import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

// TODO Configure Eclipse to correct indentation on save.

public class JSONWriter {

	private static final char TAB = '\t';
	private static final char END = '\n';

	public static void writeNestedObject(Path outFile, Map<String, TreeMap<String, TreeSet<Integer>>> map) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));) {
			writer.write("{" + END);

			int count1 = 1;
			int size1 = map.keySet().size();
			for (String word : map.keySet()) {
				writer.write(JSONWriter.tab(1) + JSONWriter.quote(word) + ": {" + END);

				int count2 = 1;
				int size2 = map.get(word).keySet().size();
				for (String file : map.get(word).keySet()) {
					writer.write(JSONWriter.tab(2) + JSONWriter.quote(file) + ": [" + END);

					int count3 = 1;
					int size3 = map.get(word).get(file).size();
					for (Integer position : map.get(word).get(file)) {
						writer.write(JSONWriter.tab(3) + position + JSONWriter.addComma(count3, size3) + END);
						count3++;
					}

					writer.write(JSONWriter.tab(2) + "]" + JSONWriter.addComma(count2, size2) + END);
					count2++;
				}

				writer.write(JSONWriter.tab(1) + "}" + JSONWriter.addComma(count1, size1) + END);
				count1++;
			}

			writer.write("}" + END);
		}   
		catch (Exception e) {
			System.err.println("Error in writeNestedObject");
		}
	}

	private static String addComma(int count, int size) {
		if (count < size) {
			return ",";
		}
		else {
			return "";
		}
	}

	private static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	private static String tab(int n) {
		char[] tabs = new char[n];
		Arrays.fill(tabs, TAB);
		return String.valueOf(tabs);
	}
}