public class HashTable {
	private final int m2;
	private final HashList[] table;
	
	public HashTable(String m2) {
		try {
			this.m2 = Integer.parseInt(m2);
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("m2 is not a number.", e);
		}

		this.table = new HashList[this.m2];
	}
	
	public void updateTable(String filePath) {
		Utils.iterateFileLines(filePath, password -> this.insert(password));
	}

	public boolean contains(String password) {
		int key = Utils.hornerPassword(password);
		return this.contains(key);
	}

	public boolean contains(int key) {
		int hash = this.hashFunction(key);
		HashList list = this.table[hash];
		return list != null && list.contains(key);
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
		list.addLast(key);
	}
	
	private int hashFunction(int key) {
		// use mod only on the positive part of key
		// because we want a positive index
		return (key & 0x7fffffff) % this.m2;
	}

	public String getSearchTime(String filePath) {
		long startNanoTime = System.nanoTime();
		Utils.iterateFileLines(filePath, password -> this.contains(password));
		long endNanoTime = System.nanoTime();
		return Utils.formatMillisecondsDiff(startNanoTime, endNanoTime);
	}
}
