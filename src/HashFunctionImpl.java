/**
 * A hash function which is used by BloomFilter
 *
 */
public class HashFunctionImpl implements HashFunction {
	public int alpha;
	public int beta;
	public int m1;
	
	public HashFunctionImpl(int a, int b, int m1) {
		this.alpha = a;
		this.beta = b;
		this.m1 = m1;
	}
	
	public int hash(int k) {
		// ((a*k + b) % p) % m1
		return (((this.alpha * k) + this.beta) % Utils.p) % m1; 
	}
}
