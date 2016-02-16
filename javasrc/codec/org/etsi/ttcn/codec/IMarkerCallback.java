/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/IMarkerCallback.java $
 *              $Id: IMarkerCallback.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec;

public interface IMarkerCallback {

    public void run(String markerName, CodecBuffer leftBuf, CodecBuffer rightBuf);

}
