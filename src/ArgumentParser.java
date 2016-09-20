import java.util.HashMap;

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
    	try {
    		for (int i = 0; i < args.length; i++) {
    			if ((args[i].charAt(0) == '-') && (args[i].length() > 1)) {
    				if ((args[i+1].charAt(0) != '-') && (args[i+1].length() > 1)) {
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
    	catch (ArrayIndexOutOfBoundsException e) {
    		System.err.println("You must enter a directory's path after -dir!");
    	}
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
    	if (argumentMap.get(flag) != null) {
    		return argumentMap.get(flag);
    	}
        return null;
    }

    public String getValue(String flag, String defaultValue) {
    	if (argumentMap.get(flag) != null) {
    		return argumentMap.get(flag);
    	}
        return defaultValue;
    }

    public String toString() {
        return argumentMap.toString();
    }
}