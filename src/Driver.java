import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Goes through a given direcotry and documents all words in every text file
 * within that directory. Documents by storing each word, its file, and its
 * position in the file into an InvertedIndex class.
 */
public class Driver {

	/**
	 * Takes in a "-dir" flag and a following directory, and starts traversing
	 * within said directory. Takes in an optional "-index" flag and a following
	 * path representing the location as to where the JSONWriter will output
	 * it's data. If and "-index" flag without a following path is provided, the
	 * JSON data will be printed at the default path: "index.json". If no
	 * "-index" flag is provided, the InvertedIndex's data will not be printed
	 * in JSON at all.
	 * 
	 * @param args
	 *            the flags and corresponding data used in creating and
	 *            utilizing an InvertedIndex
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex();

		if (parser.hasFlag("-dir")) {
			try {
				Path path = Paths.get(parser.getValue("-dir"));
				InvertedIndexBuilder.traverse(path, index);
			} catch (IOException e) {
				System.err.println("Invalid path."); // TODO Unable to traverse path + path
			} catch (NullPointerException e) {
				System.err.println("Enter a directory after the \"dir\" flag.");
			}
		}
		
		// TODO Project 2: if has -exact flag, -query? -partial? flag

		if (parser.hasFlag("-index")) {
			try {
				Path outFile = Paths.get(parser.getValue("-index", "index.json"));
				index.toJSON(outFile);
			} catch (Exception e) {
				System.err.println("Invalid path."); // TODO Unable to write to path + outFile
			}
		}
		
		// TODO Project 2: if has whatever flag it was i said for writing search output
		// TODO Project 2: add a class that handles parsing the query files
	}
}