/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/HashedId8.java $
 *              $Id: HashedId8.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Octetstring;

public class HashedId8 extends Octetstring {
    
    public HashedId8(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("HashedId8Len", "8"); 
    }
    
} // End of class HashedId8