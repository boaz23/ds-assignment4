import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public class Utils {
    public static int p = 15486907;
    public static int BYTE_BASE = 256;
    
    /**
     * Hash the given string to a integer using horner's rule
     * on the bytes composing the string
     * @param password The password
     * @return
     */
    public static int hornerPassword(String password) {
    	if (password == null) {
    		throw new RuntimeException("password is null.");
    	}
    	
    	return hornerPassword(password.getBytes());
    }
    
    /**
     * Horner's rule algorithm for calculating a polynomial value modulo p.
     * @param bytes The bytes array. We can think of the byte array as
     * P(x) = a_n*x^n+...+a_1*x+a_0 when x=256 and a_n = bytes[0], ..., a_1=bytes[bytes.length -1].
     * We then apply horner's rule for p.
     * @return The 'polynomial' (represented by the byte array)  modulo p when x=256
     */
    public static int hornerPassword(byte[] bytes) {
    	if (bytes == null) {
    		throw new RuntimeException("bytes is null.");
    	}
    	
    	long horner;
    	if (bytes.length == 0) {
    		horner = 0;
    	}
    	else {
    		horner = bytes[0];
    		for (int i = 1; i < bytes.length; i++) {
    			// (A * B) mod C = ((A mod C) * (B mod C)) mod C
    			horner = bytes[i] + (((horner % p) * (BYTE_BASE % p)) % p);
    		}
    	}
    	
    	return (int)horner;
    }
    
    public static void iterateFileLines(String filePath, Consumer<String> action) {
    	if (filePath == null || filePath.equals("")) {
    		throw new RuntimeException("filePath is null or empty.");
    	}
    	if (action == null) {
    		throw new RuntimeException("action is null.");
		}
    	
    	BufferedReader reader = null;
    	try {
	    	reader = new BufferedReader(new FileReader(filePath));
	    	reader.lines().forEachOrdered(action);
    	}
    	catch (UncheckedIOException e) {
    		throw new RuntimeException("io exception", e);
    	}
    	catch (FileNotFoundException e) {
    		throw new RuntimeException("file not found", e);
    	}
    	finally {
    		closeReader(reader);
    	}
    }

	private static void closeReader(BufferedReader reader) {
		if (reader != null) {
			try {
				reader.close();
			}
			catch (IOException e) {
				throw new RuntimeException("io exception", e);
			}
		}
	}
}
