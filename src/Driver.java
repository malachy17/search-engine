import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Goes through a given directory and documents all words in every text file
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
		TreeMap<String, ArrayList<SearchResult>> resultMap = null;

		if (parser.hasFlag("-dir")) {
			try {
				Path path = Paths.get(parser.getValue("-dir"));
				InvertedIndexBuilder.traverse(path, index);
			} catch (IOException e) {
				System.err.println("Unable to traverse path.");
			} catch (NullPointerException e) {
				System.err.println("Enter a directory after the \"dir\" flag.");
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				Path outFile = Paths.get(parser.getValue("-index", "index.json"));
				index.toJSON(outFile);
			} catch (Exception e) {
				System.err.println("Unable to write to path + outputFile.");
			}
		}

		if (parser.hasFlag("-exact")) {
			try {
				Path file = Paths.get(parser.getValue("-exact"));
				resultMap = QueryHelper.parseQueryExact(file, index);
			} catch (IOException e) {
				System.err.println("Unable to use path.");
			} catch (NullPointerException e) {
				System.err.println("Some data was unretrievable.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (parser.hasFlag("-query")) {
			try {
				Path file = Paths.get(parser.getValue("-query"));
				resultMap = QueryHelper.parseQueryPartial(file, index);
			} catch (IOException e) {
				System.err.println("Unable to use path.");
			} catch (NullPointerException e) {
				System.err.println("Some data was unretrievable.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (parser.hasFlag("-results")) {
			try {
				Path outFile = Paths.get(parser.getValue("-results", "results.json"));
				JSONWriter.writeSearchResults(outFile, resultMap);
			} catch (IOException e) {
				System.err.println("Unable to use path.");
			} catch (NullPointerException e) {
				System.err.println("Some data was unretrievable.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}