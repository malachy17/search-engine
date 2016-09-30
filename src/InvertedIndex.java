import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	// TODO Make this final
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	public InvertedIndex() {
		index = new TreeMap<>();
	}
	
	// TODO add()
	public void considerAdding(String word, String file, Integer position) {
		// If the index does not contain the word, add the word, file, and position.
		if (!index.containsKey(word)) {
			add(word, file, position, 1);
		}
		// If if the word does not contain the file, add the file and position.
		else if (!((index.get(word)).containsKey(file))) {
			add(word, file, position, 2);
		}
		// If the file does not contain the position, add the position.
		else if (!(((index.get(word)).get(file)).contains(position))) {
			add(word, file, position, 3);
		}
		
		/*
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<>());
		}
		
		if (index.get(word).get(file) == null) {
			index.get(word).put(file, new TreeSet<>());
		}
		
		index.get(word).get(file).add(position);
		*/
	}
	
	/*
	 * TODO
	 * Instead of this add method...
	 * (1) Integrate the logic directly in the public version**
	 * (2) Split it into different methods for different "commands"
	 *     - initPositionSet()
	 *     - initLocationMap()
	 *     ...
	 */
	
	private void add(String word, String file, Integer position, int command) {
		if (command == 1) {
			TreeSet<Integer> set = new TreeSet<>();
			set.add(position);

			TreeMap<String, TreeSet<Integer>> map = new TreeMap<>();
			map.put(file, set);

			index.put(word, map);
		}
		else if (command == 2) {
			TreeSet<Integer> set = new TreeSet<>();
			set.add(position);
			
			(index.get(word)).put(file, set);
		}
		else if (command == 3) {
			((index.get(word)).get(file)).add(position);
		}
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