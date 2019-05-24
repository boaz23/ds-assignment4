public class Main {
    public static void main(String[] args) {
		//Create the Bloom Filter.
		BloomFilter bloomFilter = contructBloomFilter("32");

		//Create the Hash Table.
		HashTable hashTable = contructHashTable("32");

		//Find the percentage of false-positives
		String falsePositivesPercent = bloomFilter.getFalsePositivePercentage(hashTable, System.getProperty("user.dir")+"/requested_passwords.txt");
		String rejectedPasswordsAmount = bloomFilter.getRejectedPasswordsAmount(System.getProperty("user.dir")+"/requested_passwords.txt");
        System.out.println(falsePositivesPercent + "\n" + rejectedPasswordsAmount);
		//Find the number of rejected passwords
		//Find the number of rejected passwords

		BTree btree = createTree("2");
		System.out.println(btree.toString());

	}
    
    private static BloomFilter contructBloomFilter(String m1) {
		BloomFilter bloomFilter = new BloomFilter(m1, System.getProperty("user.dir")+"/hash_functions.txt");
		//update the Bloom Filter's table with the bad passwords
		bloomFilter.updateTable(System.getProperty("user.dir")+"/bad_passwords.txt");
		return bloomFilter;

	}

	private static HashTable contructHashTable(String m2) {
		HashTable hashTable = new HashTable(m2);
		//update the Hash Table with the bad passwords
		hashTable.updateTable(System.getProperty("user.dir")+"/bad_passwords.txt");
		return hashTable;
	}

	private static BTree createTree(String tVal) {
		BTree btree = new BTree(tVal);
		btree.createFullTree(System.getProperty("user.dir")+"/bad_passwords.txt");
		return btree;
	}





//    private static void BitArrayTest() {
//        byte a = (byte)(1 << 7);
//        int b = (int)a;
//        b = b & 0xff;
//        a = (byte)b;
//        a = (byte)(b >> 1);
//
//
//        BitArray bitArray = new BitArray(24);
//        boolean bit = bitArray.get(0);
//        bitArray.set(0, true);
//        bit = bitArray.get(0);
//        bitArray.set(10, true);
//        bit = bitArray.get(10);
//        bitArray.set(10, false);
//        bit = bitArray.get(10);
//
//        bitArray.set(23, true);
//        bitArray.set(22, true);
//        bitArray.set(18, true);
//        String s = bitArray.toString();
//        bit = bitArray.get(23);
//        bit = bitArray.get(22);
//    }
}
