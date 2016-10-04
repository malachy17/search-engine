import java.util.HashMap;

// TODO The Javadoc comments are provided for you in the homework template!

public class ArgumentParser {

	private final HashMap<String, String> argumentMap;

	public ArgumentParser() {
		argumentMap = new HashMap<>();
	}

	public ArgumentParser(String[] args) {
		this();
		parseArguments(args);
	}

	public void parseArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (isFlag(args[i])) {
				if (i == args.length-1) {
					argumentMap.put(args[i], null);
				}
				else if (isValue(args[i+1])) {
					if (!argumentMap.containsKey(args[i])) {
						argumentMap.put(args[i], args[i+1]);
					}
					else {
						argumentMap.remove(args[i]);
						argumentMap.put(args[i], args[i+1]);
					}
				}
				else {
					argumentMap.put(args[i], null);
				}
			}
		}
	}

	public static boolean isFlag(String arg) {
		arg = arg.trim();
		return !arg.isEmpty() && arg.startsWith("-") && arg.length() > 1;
	}

	public static boolean isValue(String arg) {
		arg = arg.trim();
		return !arg.isEmpty() && !arg.startsWith("-") && (arg.length() > 0);
	}

	public int numFlags() {
		return argumentMap.size();
	}

	public boolean hasFlag(String flag) {
		if (argumentMap.containsKey(flag)) {
			return true;
		}
		return false;
	}

	public boolean hasValue(String flag) {
		if (argumentMap.get(flag) != null) {
			return true;
		}
		return false;
	}

	public String getValue(String flag) {
		return argumentMap.get(flag);
	}

	public String getValue(String flag, String defaultValue) {
		if (argumentMap.get(flag) != null) {
			return argumentMap.get(flag);
		}
		return defaultValue;
	}

	public int getValue(String flag, int defaultValue) {
		if (argumentMap.get(flag) != null) {
			if (isInt(argumentMap.get(flag))) {
				return Integer.parseInt(argumentMap.get(flag));
			}
		}
		return defaultValue;
	}

	public String toString() {
		return argumentMap.toString();
	}

	public boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
}