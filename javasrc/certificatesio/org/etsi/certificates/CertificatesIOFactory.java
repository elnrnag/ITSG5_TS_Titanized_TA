package org.etsi.certificates;

import org.etsi.certificates.io.CertificatesIO;
import org.etsi.certificates.io.ICertificatesIO;

public class CertificatesIOFactory {
    
    /**
     * The single instance of the class CertificatesIO.
     */
    private static ICertificatesIO _certIO = new CertificatesIO();
    
    /**
     * Accessor to the single instance of this class.
     * @return    The single instance of this class.
     */
    public static ICertificatesIO getInstance() {
        return _certIO;
    }
    
    /**
     * Internal ctor (For invocation by subclass constructors, typically implicit)
     */
    private CertificatesIOFactory() {
    }
    
} // End of class CertificatesIOFactory 
