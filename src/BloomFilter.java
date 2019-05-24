
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
		try {
			int alpha = Integer.parseInt(hashFunctionParams[0]);
			int beta = Integer.parseInt(hashFunctionParams[1]);
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("hash function's alpha or beta is not a number", e);
		}
		
		// add a new hash function to the list with the given alpha, beta and m1
		HashFunction hashFunction = new HashFunctionImpl(alpha, beta, this.m1);
		this.hashFunctions.addLast(hashFunction);
	}
	
	public void updateTable(String badPasswordsFilePath) {
		Utils.iterateFileLines(badPasswordsFilePath, password -> this.insert(password));
	}

	/**
	 * Inserts a new password to the bloom filter
	 * @param password The password
	 */
	public void insert(String password) {
		// convert the password to an integer using horner's rule
		int key = Utils.hornerPassword(password);
		for (HashFunction hashFunction : this.hashFunctions) {
			int index = hashFunction.hash(key);
			this.bitArray.set(index, true);
		}
	}

	/**
	 * Returns whether the password is in the bloom filter
	 * @param password The password
	 * @return
	 */
	public boolean contains(String password) {
		int key = Utils.hornerPassword(password);
		return this.contains(key);
	}


	/**
	 * Returns whether the key (converted number of a password) is in our bad password bloom filter.
	 * @param key The key
	 * @return
	 */
	public boolean contains(int key) {
		boolean contains = true;
		for (HashFunction hashFunction : this.hashFunctions) {
			int index = hashFunction.hash(key);

			// if the bit is not set for one of the hash functions,
			// then the key is not in the bloom filter
			if  (!this.bitArray.get(index)) {
				contains = false;
				break;
			}
		}

		return contains;
	}

	public String getFalsePositivePercentage(HashTable hashtable, String filePath){
		int falsePositive = 0;
		int goodPasswords = 0;

		// Iterate the file's lines
		try (FileLinesIterator linesIterator = new FileLinesIterator(filePath)) {
			for (String password : linesIterator) {
				int key = Utils.hornerPassword(password);

				// If the key is not in the hashtable, then the password
				// isn't bad
				if (!hashtable.contains(key)) {
					goodPasswords++;

					// however, if it is the bloom filter (when it's not in the hashtable),
					// then it's a false positive
					if (this.contains(key)) {
						falsePositive++;
					}
				}
			}
		}

		// need to convert at least one to double to use double division
		// instead of integer division.
		double percent = ((double)falsePositive / goodPasswords);
		return "" + percent;
	}

	public String getRejectedPasswordsAmount(String filePath){
		int badPasswords = 0;
		try (FileLinesIterator linesIterator = new FileLinesIterator(filePath)) {
			for (String password : linesIterator) {
				if (this.contains(password)) {
					badPasswords++;
				}
			}
		}

		return "" + badPasswords;
	}

}
