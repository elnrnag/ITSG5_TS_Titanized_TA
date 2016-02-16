/**
 * @author	STF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/common/org/etsi/common/Tuple.java $
 *             $Id: Tuple.java 1423 2014-05-22 13:59:50Z filatov $
 */
package org.etsi.common;

/**
 * This class implements the ITuple interface
 * @param <A> Type of the first member of the Tuple
 * @param <B> Type of the second member of the Tuple
 */
public class Tuple<A, B> implements ITuple<A, B> {

	/** 
	 * A element of the tuple
	 */
	private A _a;
	
	/** 
	 * B element of the tuple
	 */
	private B _b;
	
	/**
	 * Constructor
	 * @param a The A element of the new tuple
	 * @param b The B element of the new tuple
	 */
	public Tuple(A a, B b) {
		_a = a;
		_b = b;
	}

	/** 
	 * Retrieve the A element of the tuple
	 * @return the _a
	 */
	@Override
    public A getA() {
		return _a;
	}

	/** 
	 * Retrieve the B element of the tuple
	 * @return the _b
	 */
	@Override
    public B getB() {
		return _b;
	}
}
