public class SearchResult implements Comparable<SearchResult> {

	private int count;
	private int firstPosition;
	private String path;

	public SearchResult(int count, int firstPosition, String path) {
		this.count = count;
		this.firstPosition = firstPosition;
		this.path = path;
	}

	public int compareTo(SearchResult other) {

		if (Integer.compare(this.count, other.count) != 0) {
			return -1 * Integer.compare(this.count, other.count);
		}

		if (Integer.compare(this.firstPosition, other.firstPosition) != 0) {
			return Integer.compare(this.firstPosition, other.firstPosition);
		}

		return this.path.compareToIgnoreCase(other.path);
	}

	public int getCount() {
		return this.count;
	}

	public int getFirstPosition() {
		return this.firstPosition;
	}

	public String getPath() {
		return this.path;
	}

}
