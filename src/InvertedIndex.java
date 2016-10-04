import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	public InvertedIndex() {
		index = new TreeMap<>();
	}
	
	public void add(String word, String file, Integer position) {
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<>());
		}
		if (index.get(word).get(file) == null) {
			index.get(word).put(file, new TreeSet<>());
		}
		index.get(word).get(file).add(position);
	}
	
	public void toJSON(Path output) throws IOException {
		JSONWriter.writeNestedObject(output, index);
	}
}