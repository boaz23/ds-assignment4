public class BloomFilter {
	private int m1;
	private BitArray bitArray;
	private LinkedList<HashFunction> hashFunctions;
	
	public BloomFilter(String m1, String hashFunctionsFilePath) {
		// parse the string to an integer
		int i32M1 = Integer.parseInt(m1);
		this.m1 = i32M1;
		
		// initialize the bit array
		this.bitArray = new BitArray(i32M1);

		this.hashFunctions = new LinkedList<>();
		
		// Iterate the lines of the file and parse each line
		FileLinesIterator linesIterator = null;
		try {
			linesIterator = new FileLinesIterator(hashFunctionsFilePath);
			while (linesIterator.hasNext()) {
				String line = linesIterator.next();
				String[] hashFunctionParams = line.split("_");
				String sAlpha = hashFunctionParams[0];
				String sBeta = hashFunctionParams[1];
				
				// add a new hash function to the list with the given alpha, beta and m1
				this.hashFunctions.add(new HashFunctionImpl(Integer.parseInt(sAlpha), Integer.parseInt(sBeta), i32M1));
			}
		}
		finally {
			if (linesIterator != null) {
				linesIterator.close();
			}
		}
	}
	
	public void updateTable(String badPasswordsFilePath) {
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
