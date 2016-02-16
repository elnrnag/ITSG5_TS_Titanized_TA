/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/certificatesio/org/etsi/certificates/io/ICertificatesIO.java $
 *              $Id: ICertificatesIO.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.certificates.io;

import java.io.ByteArrayOutputStream;

/**
 * 
 * @desc Interface to load certificates/private keys from disk
 *
 */
public interface ICertificatesIO {
    
    /**
     * @desc    Load in memory cache the certificates available in the specified directory
     * @param   rootDirectory Root directory to access to the certificates identified by the certificate ID
     * @param   configId      A configuration identifier
     * @return  true on success, false otherwise
     */
    boolean loadCertificates(final String rootDirectory, final String configId);
    
    /**
     * @desc    Unload from memory cache the certificates
     * @return  true on success, false otherwise
     */
    boolean unloadCertificates();
    
    /**
     * @desc    Read the specified certificate
     * @param   certificateId the certificate identifier
     * @param   certificate   the expected certificate
     * @return  true on success, false otherwise
     */
    boolean readCertificate(final String certificateId, final ByteArrayOutputStream certificate);
    
    /**
     * @desc    Read the specified certificate digest
     * @param   certificateId the certificate identifier
     * @param   digest        the expected digest
     * @return  true on success, false otherwise
     */
    boolean readCertificateDigest(final String certificateId, final ByteArrayOutputStream digest);
    
    /**
     * @desc    Read the signing private key for the specified certificate
     * @param   keysId  the keys identifier
     * @param   key     the signing private key
     * @return  true on success, false otherwise
     */
    boolean readSigningKey(final String keysName, final ByteArrayOutputStream key);

    /**
     * @desc    Read the encryption private key for the specified certificate
     * @param   keysId  the keys identifier
     * @param   key     the encrypt private key
     * @return  true on success, false otherwise
     */
    boolean readEncryptingKey(final String keysName, final ByteArrayOutputStream key);

    /**
     * @desc    Retrieve the key identifier associated to the Digest value
     * @param   p_hashedId8ToBeUsed            the Digest value
     * @return  The key identifier
     */
    String getKeyIdFromHashedId8(final  byte[] p_hashedId8ToBeUsed);
    
} // End of interface ICertificatesIO
