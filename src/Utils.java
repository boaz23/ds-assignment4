public class Utils {
    public static int p = 15486907;
    public static int BYTE_BASE = 256;
    
    /**
     * Hash the given string to a integer using horner's rule
     * while treating each byte of the string as a number of base 256
     * @param password The password
     * @return
     */
    public static int hashPassword(String password) {
    	return hashBytes(password.getBytes());
    }
    
    /**
     * Horner's rule algorithm for calculating a polynomial value modulo p
     * @param bytes
     * @return
     */
    public static int hashBytes(byte[] bytes) {
    	long hash;
    	if (bytes.length == 0) {
    		hash = 0;
    	}
    	else {
    		hash = bytes[0];
    		for (int i = 1; i < bytes.length; i++) {
    			hash = bytes[i] + (((hash % p) * (BYTE_BASE % p)) % p);
    		}
    	}
    	
    	return (int)hash;
    }
}
