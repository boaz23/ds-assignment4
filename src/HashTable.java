public class HashTable {
	private int m2;
	private HashList[] table;
	
	public HashTable(String m2) {
		this.m2 = Integer.parseInt(m2);
		this.table = new HashList[this.m2];
	}
	
	public void updateTable(String badPasswordsFilePath) {
    	if (badPasswordsFilePath == null || badPasswordsFilePath.equals("")) {
    		throw new RuntimeException("badPasswordsFilePath is null or empty.");
    	}
		Utils.iterateFileLines(badPasswordsFilePath, password -> this.insert(password));
	}

	public void Requstedpass (String requestedPasswordsFilePath) {
		if (requestedPasswordsFilePath == null || requestedPasswordsFilePath.equals("")) {
			throw new RuntimeException("badPasswordsFilePath is null or empty.");
		}
		Utils.iterateFileLines(requestedPasswordsFilePath, password -> this.insert(password));
	}

	public boolean contains(String password) {
		int key = Utils.hornerPassword(password);
		int hash = this.hashFunction(key);
		HashList list;
		list = this.table[hash];
		return list !=null && list.contains(key);
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
	
	public int hashFunction(int key) {
		// use mod only on the positive part of key
		// because we want a positive index
		return (key & 0x7fffffff) % this.m2;
	}
}
