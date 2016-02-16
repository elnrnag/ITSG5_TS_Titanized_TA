/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/TERFactory.java $
 *              $Id: TERFactory.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec;

public class TERFactory {

    private static ITERequired instance; 

    public static ITERequired getInstance() {
        return instance;
    }

    private TERFactory() {

    }

    public static void setImpl(ITERequired impl) {
        instance = impl;
    }
}
