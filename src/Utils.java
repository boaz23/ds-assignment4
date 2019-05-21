public class Utils {
    public static int p = 15486907;
    public static int BYTE_BASE = 256;
    
    /**
     * Hash the given string to a integer using horner's rule
     * while treating each byte of the string as a number of base 256
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
     * Horner's rule algorithm for calculating a polynomial value modulo p
     * @param bytes
     * @return
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
}
