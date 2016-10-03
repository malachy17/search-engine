import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	public InvertedIndex() {
		index = new TreeMap<>();
	}
	
	public void add(String word, String file, Integer position) {
		// If the index does not contain the word, add the word, file, and position.
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<>());
		}
		// If if the word does not contain the file, add the file and position.
		if (index.get(word).get(file) == null) {
			index.get(word).put(file, new TreeSet<>());
		}
		// If the file does not contain the position, add the position.
		index.get(word).get(file).add(position);
	}
	
	// TODO Can't break encapsulation!
	
	public Set<String> getWordSet() {
		return index.keySet();
	}
	
	public Set<String> getFileSet(String word) {
		return index.get(word).keySet();
	}
	
	public Set<Integer> getIntegerSet(String word, String file) {
		return index.get(word).get(file);
	}
}