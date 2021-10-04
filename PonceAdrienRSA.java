import java.util.Random;

/**
 * CS327 Project 1.
 * 
 * @author Adrien Ponce
 * @version 9/21/21
 */
public class PonceAdrienRSA {

	/**
	 * Takes two input integers and returns their greatest common
	 * divisor. Only iterative solutions are allowed.
	 * 
	 * @param inE the first int
	 * @param inZ the second int
	 * @return the greatest common divisor
	 */
	public static int gcd(int inE, int inZ)
	{
		int biggest = 0;
		int smallest = 0;
		
		// error checking
		if (inE == 0) 
		{
			return inZ;
		} else if (inZ == 0) {
			return inE;
		}
		
		// grab the biggest value to compute Euclidean gcd
		if (inE >= inZ) 
		{
			biggest = inE;
			smallest = inZ;
		} else {
			biggest = inZ;
			smallest = inE;
		}
		
		int d = 0; // remainder
		
		d = biggest % smallest;
		int temp = 0; // temp var to store remainder
		
		// loop until d = 0;
		while (d > 0)
		{
			temp = d;
			d = smallest % d;
			smallest = temp; // update new smallest 
		}
		
		return temp;
	}
	
	public void testGcd()
	{
		int result1 = gcd(29, 288);
		int result2 = gcd(30, 288);
		
		System.out.println ("GCD (29, 288) = 0x" + Integer.toString(result1, 16));
		System.out.println ("GCD (30, 288) = 0x" + Integer.toString(result2, 16));
	}
	
	/**
	 * Takes two input integers and returns the multiplicative inverse of
	 * e modulo z. If the multiplicative inverse of e DNE, then return -1.
	 * 
	 * @param inE the first int
	 * @param inZ the second int
	 * @return the multiplicative inverse of e modulo z
	 */
	public static int xgcd(int inE, int inZ)
	{	
		int d = 0; // remainder
		int q = 0; // floor		
		d = inZ % inE;
		q = inZ / inE;
		
		int older = 0; // first xgcd component
		int old = 1; // second xgcd component 
		int tTemp = 0; // third xgcd component
		
		// swap old and older similar to the table
		tTemp = old * q;
		tTemp = older - tTemp;
		older = old;
		old = tTemp;
		
		int temp = 0;
		
		// loop until d = 0;
		while (d > 0)
		{
			// gcd calculations
			temp = d;
			d = inE % d;
			q = inE / temp;	
			
			// swap old and older each iteration
			tTemp = old * q;
			tTemp = older - tTemp;
			older = old;
			old = tTemp;	
			
			inE = temp; // update new smallest 
		}
		
		// this is the t final solution
		if (older < 0)
		{
			older = older + inZ;
		}
		
		// check that xgcd exists
		if (temp != 1) {
			return -1;
		}
		
		return older;
	}
	
	public void testXgcd () 
	{
		int result1 = xgcd(29, 288);
		int result2 = xgcd(149, 288);

		System.out.println ("29^-1 mod 288 = 0x" + Integer.toString(result1, 16));
		System.out.println ("149^-1 mod 288 = 0x" + Integer.toString(result2, 16));
		
	}
	
	/**
	 * Takes three input integers and returns the RSA public/private key pair
	 * (e, N, d). Check if inE is valid. 
	 * 
	 * @param inP large prime P
	 * @param inQ large prime Q
	 * @param inE coprime to z and 1 < e < Z ((p -1) * (q - 1))
	 * @return
	 */
	static int[] keygen(int inP, int inQ, int inE)
	{
		int inN = inP * inQ;
		int inZ = (inP - 1) * (inQ - 1); // bounds
		int inD = 0;
		
		int newInE = inE;
		Random rand = new Random();
		
		// check if inE is valid (1 < inE < Z && gcd(inE, inZ) == 1)
		while (newInE <= 1 && gcd(newInE, inZ) != 1)
		{
			newInE = rand.nextInt(inZ);
		}
		
		// check coprime
		if (inE > 1 && inE < inZ && gcd(inE, inZ) == 1) {
			newInE = inE;
		}
		
		inD = xgcd(newInE, inZ);
		int[] sol = new int[] {newInE, inN, inD};
		return sol;
	}
	
	/*
	 * The following method will return an integer array, with [e, N, d] in this order
	 */
	public void testKeygen () {
		int[] keypair = keygen (17, 19, 29);

		System.out.println ("e = 0x" + Integer.toString(keypair[0], 16));
		System.out.println ("N = 0x" + Integer.toString(keypair[1], 16));
		System.out.println ("d = 0x" + Integer.toString(keypair[2], 16));
	}
	
	/**
	 * Square and multiply method. c = a^b mod n
	 * 
	 * @param a getting the exponent
	 * @param b exponent
	 * @param n modulus
	 * @return square and multiply
	 */
	public int modExpOne (int a, int b, int n) 	 {

		int x = 1;
		int w = a; 
		int y = b;
		
		while (y > 0) {
			int t = y%2; 
			y = y/2;
			if (t == 1) { 
				long xLong = x * w; 
				x = (int) (xLong % n);
			}
			
			long wLong = w * w;
			w = (int) (wLong % n);
		}

		return x;
	}
	
	/**
	 * Takes three input integers and returns c = m^e mod N
	 * SHOULD USE THE SQUARE AND MULTIPLY ALGORITHM
	 * 
	 * @param message the message to encrypt using ASCII encoding
	 * @param inE the first integer
	 * @param inN the second integer
	 * @return c = m^e mod N
	 */
	public int encrypt(int message, int inE, int inN)
	{
		return modExpOne(message, inE, inN);
	}
	
	/**
	 * Take three input integers and returns c = c^d mod N
	 * 
	 * @param ciphertext the text to decrypt
	 * @param inD the first integer
	 * @param inN the second integer
	 * @return c = c^d mod N
	 */
	public int decrypt(int ciphertext, int inD, int inN) 
	{
		return modExpOne(ciphertext, inD, inN);
	}
	
	public void testRSA() {
		int[] keypair = keygen (17, 19, 29);

		int m1 = 4;
		int c1 = encrypt (m1, keypair[0], keypair[1]);
		System.out.println ("The encryption of (m1=0x" + Integer.toString(m1, 16) 
		+ ") is 0x" + Integer.toString(c1, 16));
		int cleartext1 = decrypt (c1, keypair[2], keypair[1]);
		System.out.println ("The decryption of (c=0x" + Integer.toString(c1, 16) 
		+ ") is 0x" + Integer.toString(cleartext1, 16));

		int m2 = 5;
		int c2 = encrypt (m2, keypair[0], keypair[1]);
		System.out.println ("The encryption of (m2=0x" + Integer.toString(m2, 16) 
		+ ") is 0x" + Integer.toString(c2, 16));
		int cleartext2 = decrypt (c2, keypair[2], keypair[1]);
		System.out.println ("The decryption of (c2=0x" + Integer.toString(c2, 16) 
		+ ") is 0x" + Integer.toString(cleartext2, 16));
	}
	
	/**
	 * Tests the above methods.
	 * 
	 * @param args command-line args
	 */
	public static void main(String[] args)
	{
		PonceAdrienRSA parsa = new PonceAdrienRSA();
		
		System.out.println ("********** Project 1 output begins **********");
		
		parsa.testGcd();
		parsa.testXgcd();
		parsa.testKeygen();
		parsa.testRSA();
	}
}
