/**
 * @author	STF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/common/org/etsi/common/ITuple.java $
 *             $Id: ITuple.java 748 2012-01-23 14:51:03Z tepelmann $
 */
package org.etsi.common;

/**
 * This interface provides mandatory method to be implemented by a Tuple {A, B}
 * @param <A> Type of the first member of the Tuple
 * @param <B> Type of the second member of the Tuple
 */
public interface ITuple<A, B> {

	/** Retrieve the A element of the tuple
	 * @return the _a
	 */
	public abstract A getA();

	/** Retrieve the B element of the tuple
	 * @return the _b
	 */
	public abstract B getB();

}