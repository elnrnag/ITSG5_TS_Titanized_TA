/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/ITERequired.java $
 *              $Id: ITERequired.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec;

import org.etsi.ttcn.tci.Type;

public interface ITERequired {

    public Type getTypeForName(String typeName);
    public Type getInteger();
    public Type getFloat();
    public Type getBoolean();
    public Type getCharstring();
    public Type getUniversalCharstring();
    public Type getHexstring();
    public Type getBitstring();
    public Type getOctetstring();
    public Type getVerdict();
    public void tciErrorReq(String message);
}
