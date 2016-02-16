/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Dummy.java $
 *              $Id: Dummy.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Dummy extends ICodec {

    public Dummy(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {

        return decodingHypothesis.newInstance();
    }

    @Override
    public CodecBuffer encode(Value value) {

        return new CodecBuffer(new byte[]{(byte)0x00});
    }
}