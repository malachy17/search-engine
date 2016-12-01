import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiInvertedIndexBuilder {

	private static final Logger logger = LogManager.getLogger();

	private final InvertedIndex index;

	private final WorkQueue minions;
	private int pending;

	public MultiInvertedIndexBuilder(InvertedIndex index, int threads) {
		this.index = index;
		this.minions = new WorkQueue(threads);
		this.pending = 0;
	}

	/**
	 * Traverses a given directory and goes through every file. If the file ends
	 * with ".txt", then it hands off that file and the index to the parseFile
	 * method.
	 * 
	 * @param path
	 *            the directory to start traversing from
	 * @param index
	 *            the inverted index to hand off to the parseFile method.
	 * @throws IOException
	 */
	public void traverse(Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					traverse(file);
				} else {
					if (file.getFileName().toString().toLowerCase().endsWith(".txt")) {
						// parseFile(file);
						minions.execute(new Minion(file));
					}
				}
			}
		} catch (IOException e) {
			logger.warn("Unable to traverse {}", path);
			logger.catching(Level.DEBUG, e);
		}
	}

	// TODO Remember, copy/paste is bad :(
	// TODO InvertedIndexBuilder.parseFile(Path, InvertedIndex)
	/**
	 * Parses a given text file and the words in each line. Each legal word is
	 * added to the given index.
	 * 
	 * @param input
	 *            the file being parsed
	 * @param index
	 *            the InvertedIndex data structure that will add in each word.
	 * @throws IOException
	 */
	public void parseFile(Path input) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));) {
			String line = null;
			int position = 1;

			String location = input.normalize().toString();

			while ((line = reader.readLine()) != null) {
				line = InvertedIndexBuilder.clean(line);
				String[] words = line.split("\\s+");

				for (String word : words) {
					if (!word.isEmpty()) {
						index.add(word.trim(), location, position);
						position++;
					}
				}
			}
		}
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link Minion} is created to handle that subdirectory.
	 */
	private class Minion implements Runnable {

		private Path file;

		public Minion(Path file) {
			logger.debug("Minion created for {}", file);
			this.file = file;

			// Indicate we now have "pending" work to do. This is necessary
			// so we know when our threads are "done", since we can no longer
			// call the join() method on them.
			incrementPending();
		}

		@Override
		public void run() {
			try {
				parseFile(file);
				
				// TODO Avoid constant blocking operations
				/*
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.parseFile(file, local);
				index.addAll(local);
				*/
				
				// Indicate that we no longer have "pending" work to do.
				decrementPending();
			} catch (IOException e) {
				logger.warn("Unable to parse {}", file);
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Minion finished {}", file);
		}
	}

	private synchronized void incrementPending() {
		pending++;
		logger.debug("incrementPending(): Pending is now {}", pending);
	}

	private synchronized void decrementPending() {
		pending--;
		logger.debug("decrementPending(): Pending is now {}", pending);

		if (pending <= 0) {
			this.notifyAll();
		}
	}

	public synchronized void finish() {
		try {
			while (pending > 0) {
				logger.debug("finish(): Waiting until finished.");
				this.wait();
			}
		} catch (InterruptedException e) {
			logger.debug("finish(): Finish interrupted", e);
		}
	}

	public synchronized void shutdown() {
		logger.debug("Shutting down");
		finish();
		minions.shutdown();
	}

}
