public class BloomFilter {
	private int m1;
	private BitArray bitArray;
	private LinkedList<HashFunction> hashFunctions;
	
	public BloomFilter(String m1, String hashFunctionsFilePath) {
    	if (hashFunctionsFilePath == null || hashFunctionsFilePath.equals("")) {
    		throw new RuntimeException("hashFunctionsFilePath is null or empty.");
    	}
    	
		// parse the string to an integer
		try {
			this.m1 = Integer.parseInt(m1);
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("m1 is not a number is base 10", e);
		}
		
		// initialize the bit array
		this.bitArray = new BitArray(this.m1);
		this.hashFunctions = new LinkedList<>();
		Utils.iterateFileLines(hashFunctionsFilePath, password -> this.parseHashFunctionLine(password));
	}

	private void parseHashFunctionLine(String line) {
		String[] hashFunctionParams = line.split("_");
		String sAlpha = hashFunctionParams[0];
		String sBeta = hashFunctionParams[1];
		
		// add a new hash function to the list with the given alpha, beta and m1
		HashFunction hashFunction = new HashFunctionImpl(Integer.parseInt(sAlpha), Integer.parseInt(sBeta), this.m1);
		this.hashFunctions.addLast(hashFunction);
	}
	
	public void updateTable(String badPasswordsFilePath) {
		Utils.iterateFileLines(badPasswordsFilePath, password -> this.insert(password));
	}
	
	public void insert(String password) {
		// convert the password to an integer using horner's rule
		int key = Utils.hornerPassword(password);
		for (HashFunction hashFunction : this.hashFunctions) {
			int index = hashFunction.hash(key);
			this.bitArray.set(index, true);
		}
	}
}
