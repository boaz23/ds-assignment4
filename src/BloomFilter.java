import java.io.BufferedReader;
import java.io.IOException;

public class BloomFilter {
    private int m1;
    private BitArray bitArray;
    private LinkedList<HashFunction> hashFunctions;

    /**
     * Initializes a new bloom filter
     * @param m1 The size of the bloom filter in bits
     * @param filePath The file path to read hash functions from
     */
    public BloomFilter(String m1, String filePath) {
        if (filePath == null || filePath.equals("")) {
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
        Utils.iterateFileLines(filePath, password -> this.parseHashFunctionLine(password));
    }

    /**
     * Parses a hash function line and adds the hash function to the
     * hash functions list
     * @param line The line to parse
     */
    private void parseHashFunctionLine(String line) {
        try {
            String[] hashFunctionParams = line.split("_");
            int alpha = Integer.parseInt(hashFunctionParams[0]);
            int beta = Integer.parseInt(hashFunctionParams[1]);

            // add a new hash function to the list with the given alpha, beta and m1
            HashFunction hashFunction = new HashFunctionImpl(alpha, beta, this.m1);
            this.hashFunctions.addLast(hashFunction);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("hash function's alpha or beta is not a number", e);
        }
    }

    public void updateTable(String filePath) {
        Utils.iterateFileLines(filePath, password -> this.insert(password));
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
     */
    public boolean contains(String password) {
        int key = Utils.hornerPassword(password);
        return this.contains(key);
    }

    /**
     * Returns whether the key (converted number of a password) is in our bad password bloom filter.
     * @param key The key
     */
    private boolean contains(int key) {
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

    public String getFalsePositivePercentage(final HashTable hashtable, String filePath) {
        return Utils.consumeFileReader(filePath, reader -> getFalsePositivePercentage(hashtable, reader));
    }

    public String getRejectedPasswordsAmount(String filePath) {
        return Utils.consumeFileReader(filePath, reader -> getRejectedPasswordsAmount(reader)).toString();
    }

    /**
     * Returns the amount of passwords rejected by this bloom filter
     * @param reader The reader of the file
     */
    private int getRejectedPasswordsAmount(BufferedReader reader) throws IOException {
        int badPasswords = 0;

        String password;
        while ((password = reader.readLine()) != null) {
            if (this.contains(password)) {
                badPasswords++;
            }
        }

        return badPasswords;
    }

    /**
     * Returns the amount false positive (passwords that are ok but are still rejected by the bloom filter)
     * @param hashtable The hashtable that contains the bad passwords
     * @param reader The file reader
     */
    private String getFalsePositivePercentage(HashTable hashtable, BufferedReader reader) throws IOException {
        int falsePositive = 0;
        int goodPasswords = 0;

        String password;
        while ((password = reader.readLine()) != null) {
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

        // need to convert at least one to double to use double division
        // instead of integer division.
        double percent = ((double)falsePositive / goodPasswords);
        return "" + percent;
    }
}
