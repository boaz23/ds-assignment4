public class HashTable {
	private int m2;
	private HashList[] table;
	
	public HashTable(String m2) {
		int i32M2 = Integer.parseInt(m2);
		this.table = new HashList[i32M2];
		this.m2 = i32M2;
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
		int key = Utils.hornerPassword(password);
		int hash = this.hashFunction(key);
		
		HashList list;
		// initialize a new list if we haven't yet
		if (this.table[hash] == null) {
			list = new HashList();
			this.table[hash] = list;
		}
		else {
			// we have a linked list already, get it
			list = this.table[hash];
		}
		
		// add a new element to the list
		list.addLast(new HashListElement(key, password));
	}
	
	public int hashFunction(int key) {
		// use mod only on the positive part of key
		// because we want a positive index
		return (key & 0x7fffffff) % this.m2;
	}
}
