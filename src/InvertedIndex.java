import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	public InvertedIndex() {
		index = new TreeMap<>();
	}
	
	public void considerAdding(String word, String file, Integer position) {
		// If the index does not contain the word, add the word, file, and position.
		if (!index.containsKey(word)) {
			considerAdding(word, file, position, 1);
		}
		// If if the word does not contain the file, add the file and position.
		else if (!((index.get(word)).containsKey(file))) {
			considerAdding(word, file, position, 2);
		}
		// If the file does not contain the position, add the position.
		else if (!(((index.get(word)).get(file)).contains(position))) {
			considerAdding(word, file, position, 3);
		}
	}
	
	public void considerAdding(String word, String file, Integer position, int command) {
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
}
	
