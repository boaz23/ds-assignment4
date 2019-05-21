public class BloomFilter {
	private BitArray bitArray;
	private LinkedList<HashFunction> hashFunctions;
	
	public BloomFilter(String m1, String hashFunctionsFilePath) {
    	if (hashFunctionsFilePath == null || hashFunctionsFilePath.equals("")) {
    		throw new RuntimeException("hashFunctionsFilePath is null or empty.");
    	}
    	
		// parse the string to an integer
		int i32M1;
		try {
			i32M1 = Integer.parseInt(m1);
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("m1 is not a number is base 10", e);
		}
		
		// initialize the bit array
		this.bitArray = new BitArray(i32M1);
		this.hashFunctions = new LinkedList<>();
		this.parseHashFunctionsFile(hashFunctionsFilePath, i32M1);
	}

	private void parseHashFunctionsFile(String hashFunctionsFilePath, int i32M1) {
		// Iterate the lines of the file and parse each line
		FileLinesIterator linesIterator = null;
		try {
			linesIterator = new FileLinesIterator(hashFunctionsFilePath);
			while (linesIterator.hasNext()) {
				String line = linesIterator.next();
				this.parseHashFunctionLine(i32M1, line);
			}
		}
		finally {
			if (linesIterator != null) {
				linesIterator.close();
			}
		}
	}

	private void parseHashFunctionLine(int i32M1, String line) {
		String[] hashFunctionParams = line.split("_");
		String sAlpha = hashFunctionParams[0];
		String sBeta = hashFunctionParams[1];
		
		// add a new hash function to the list with the given alpha, beta and m1
		this.hashFunctions.add(new HashFunctionImpl(Integer.parseInt(sAlpha), Integer.parseInt(sBeta), i32M1));
	}
	
	public void updateTable(String badPasswordsFilePath) {
    	if (badPasswordsFilePath == null || badPasswordsFilePath.equals("")) {
    		throw new RuntimeException("badPasswordsFilePath is null or empty.");
    	}
    	
		// Iterate the lines of the file and parse each line
		FileLinesIterator linesIterator = null;
		try {
			linesIterator = new FileLinesIterator(badPasswordsFilePath);
			while (linesIterator.hasNext()) {
				String line = linesIterator.next();
				
				// insert the password to the bloom filter
				this.insert(line);
			}
		}
		finally {
			if (linesIterator != null) {
				linesIterator.close();
			}
		}
	}
	
	public void insert(String password) {
		// convert the password to an integer using horner's rule
		int key = Utils.hashPassword(password);
		for (HashFunction hashFunction : this.hashFunctions) {
			int index = hashFunction.hash(key);
			this.bitArray.set(index, true);
		}
	}
}
