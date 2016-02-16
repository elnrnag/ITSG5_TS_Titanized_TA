/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/certificatesio/org/etsi/certificates/io/CertificatesIO.java $
 *              $Id: CertificatesIO.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.certificates.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.etsi.certificates.Helpers;
import org.etsi.common.ByteHelper;

import de.fraunhofer.sit.c2x.CryptoLib;

public class CertificatesIO implements ICertificatesIO {
    
    /**
     *  Extension file for certificate
     */
    private static final String CERT_EXT = "crt";
    
    /**
     *  Extension file for digests
     */
    private static final String DIGESTS_EXT = "dgs";
    
    /**
     *  Excluded files while building the lists of certificates/private keys
     */
    private static final String[] EXCLUDED_PATTERNS = new String[] { /*for debug: */".svn", "._.DS_Store", ".DS_Store"};
    
    /**
     * Full path to access certificate files
     */
    private String _fullPathCerts;
    
    /**
     * Memory cache for the certificates
     */
    private Map<String, byte[]> _cachedCertificates;
    
    private Map<String, byte[]> _cachedCertificatesDigest;
    
    private Map<String, String> _cachedReverseCertificatesDigest;
    
    /**
     * Memory cache for the signing private keys
     */
    private Map<String, byte[]> _cachedSigningPrivateKey;
    
    /**
     * Memory cache for the encrypt private keys
     */
    private Map<String, byte[]> _cachedEncryptPrivateKey;
    
    /**
     * Set to true if loadCertificates was already called
     */
    private boolean _areCertificatesLoaded = false;
    
    /**
     * Default constructor
     */
    public CertificatesIO() {
        _cachedCertificates = new ConcurrentHashMap<String, byte[]>();
        _cachedCertificatesDigest = new ConcurrentHashMap<String, byte[]>();
        _cachedSigningPrivateKey = new ConcurrentHashMap<String, byte[]>();
        _cachedEncryptPrivateKey = new ConcurrentHashMap<String, byte[]>();
        _cachedReverseCertificatesDigest = new ConcurrentHashMap<String, String>();
    } // End of Constructor
    
    /**
     * @desc    Load in memory cache the certificates available in the specified directory
     * @param   rootDirectory Root directory to access to the certificates identified by the certificate ID
     * @param   configId      A configuration identifier
     * @return  true on success, false otherwise
     */
    @Override
    public boolean loadCertificates(final String p_rootDirectory, final String p_configId) { // E.g. <rootDirectory path>, cfg01
//        System.out.println(">>> CertificatesIO.loadCertificates: " + p_rootDirectory + ", " + p_configId + " - " + _areCertificatesLoaded);
        
        // Sanity check
        if (_areCertificatesLoaded) {
            return true;
        }
        
        // Build full path
        if ((p_rootDirectory == null) || (p_rootDirectory.length() == 0)) {
            _fullPathCerts = System.getProperty("user.dir").replace("\\", "/");
        } else {
            _fullPathCerts = p_rootDirectory.replace("\\", "/");
        }
        if (!_fullPathCerts.endsWith("/")) {
            _fullPathCerts += "/";
        }
        
        File certsPath = new File(_fullPathCerts);
        if (!certsPath.exists()) {
            System.err.println("CertificatesIO.loadCertificates: path '" + _fullPathCerts + "' does not found");
            return false;
        }
        
        _areCertificatesLoaded = loadMemoryCache(certsPath); // Load certificates and keys and return

        if ((p_configId != null) && (p_configId.length() != 0)) {
            String path = new String(_fullPathCerts + "/" + p_configId); 
            certsPath = new File(path);
            if (!certsPath.exists()) {
                System.err.println("CertificatesIO.loadCertificates: path '" + path + "' does not found");
                return false;
            }
            loadMemoryCache(certsPath); // Load certificates and keys and return
        }
        
        return _areCertificatesLoaded;
    }
    
    /**
     * @desc    Unload from memory cache the certificates available
     * @return  true on success, false otherwise
     */
    @Override
    public boolean unloadCertificates() {
        _areCertificatesLoaded = false;
        _fullPathCerts = null;
        _cachedCertificates.clear();
        _cachedCertificatesDigest.clear();
        _cachedSigningPrivateKey.clear();
        _cachedEncryptPrivateKey.clear();
        _cachedReverseCertificatesDigest.clear();
        
        return true;
    }
    
    /**
     * @desc    Read the specified certificate
     * @param   certificateId the certificate identifier
     * @param   certificate   the expected certificate
     * @return  true on success, false otherwise
     */
    @Override
    public boolean readCertificate(final String key, final ByteArrayOutputStream certificate) {
//        System.out.println(">>> CertificatesIO.readCertificate: " + key);
        
        String certKey;
        if (_cachedReverseCertificatesDigest.containsKey(key)) {
            certKey = _cachedReverseCertificatesDigest.get(key);
        }else{
            certKey = key;
        }
        
        if (_cachedCertificates.containsKey(certKey)) {
            try {
                certificate.write(_cachedCertificates.get(certKey));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.err.println("CertificatesIO.readCertificate: key '" + key + "' not found");
        }
        return false;
    }
    
    @Override
    public boolean readCertificateDigest(final String certificateId, final ByteArrayOutputStream digest) {
//        System.out.println(">>> CertificatesIO.readCertificateDigest: " + certificateId);
        
        // Sanity check
        if (!_cachedCertificatesDigest.containsKey(certificateId)) {
            System.err.println("CertificatesIO.readCertificateDigest: key '" + certificateId + "' not found");
            return false;
        }
        
        try {
            digest.write(_cachedCertificatesDigest.get(certificateId));
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * @desc    Read the signing private key for the specified certificate
     * @param   keysId  the keys identifier
     * @param   key     the signing private key
     * @return  true on success, false otherwise
     */
    @Override
    public boolean readSigningKey(final String keyName, final ByteArrayOutputStream key) { 
//        System.out.println(">>> CertificatesIO.readSigningKey: " + keyName);
        
        try {
            String certKey;
            if (_cachedReverseCertificatesDigest.containsKey(keyName)) {
                certKey = _cachedReverseCertificatesDigest.get(keyName);
            }else{
                certKey = keyName;
            }
            if (_cachedSigningPrivateKey.containsKey(certKey)) {
                key.write(_cachedSigningPrivateKey.get(certKey));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * @desc    Read the encrypting private key for the specified certificate
     * @param   keysId  the keys identifier
     * @param   key     the signing private key
     * @return  true on success, false otherwise
     */
    @Override
    public boolean readEncryptingKey(final String keyName, final ByteArrayOutputStream key) { 
        String certKey;
        try {
            if (_cachedReverseCertificatesDigest.containsKey(keyName)) {
                certKey = _cachedReverseCertificatesDigest.get(keyName);
            }else{
                certKey = keyName;
            }
            if (_cachedEncryptPrivateKey.containsKey(certKey)) {
                key.write(_cachedEncryptPrivateKey.get(certKey));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * @desc Load certificates based on existing keys file, excluding xxx_at.bin files 
     * @param p_keysPath    path for private key files
     * @param p_certsPath   Path for certificate files
     * @return true on success, false otherwise
     */
    private boolean loadMemoryCache(final File p_keysPath) { // E.g. <path>/keys, <path>/certs
        // Retrieve the list of the files in the p_keysPath
        try {
            List<File> files = Helpers.getInstance().getFileListing(p_keysPath, CERT_EXT, EXCLUDED_PATTERNS);
            // Create the memory cache
            for (File file : files) {
                try {
                    addCertItem(file);
                }catch(FileNotFoundException e){}
            } // End of 'for' statement
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load digests
        try {
            List<File> files = Helpers.getInstance().getFileListing(p_keysPath, DIGESTS_EXT, EXCLUDED_PATTERNS);
            // Create the memory cache
            for (File file : files) {
                try {
                    addDigestItem(file);
                }catch(FileNotFoundException e){}
            } // End of 'for' statement
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    private void addDigestItem(final File p_file)  throws FileNotFoundException, IOException {
        String filename = p_file.getName();
        String certName = filename.substring(0, filename.lastIndexOf(".")).toUpperCase();

        // Load certificate
        byte bytes[] = new byte[64];
        FileInputStream fs = new FileInputStream(p_file);
        int n = fs.read(bytes);
        fs.close();
        
        if(n == 32){
            // take last 8 bytes
        	bytes = Arrays.copyOfRange(bytes, 24, 32);
        }else if(n >= 64){
    		bytes = ByteHelper.hexStringToByteArray(new String(bytes).substring(48, 64));
        }else if(n >= 16){
    		bytes = ByteHelper.hexStringToByteArray(new String(bytes).substring(0, 16));
        }else if(n == 8){
        	bytes = Arrays.copyOfRange(bytes, 0, 8);
        }else{
            System.err.println("CertificatesIO: " + filename + ": wrong digest file length\n");
            return;
        }
        _cachedCertificatesDigest.put(certName, bytes);
//        System.out.println("CertificatesIO.addDigestItem: Store digest: " + ByteHelper.byteArrayToString(bytes) + " - " + certName);
    }
    
    private void addCertItem(final File p_certFile)  throws FileNotFoundException, IOException {
//        System.out.println(">>> CertificatesIO.addItem: " + p_certFile);
        
        // Load the keys file name
        String filename = p_certFile.getName();
        String certName = filename.substring(0, filename.lastIndexOf(".")).toUpperCase();
        
        // Load certificate
        byte bytes[] = new byte[(int) p_certFile.length()];
        FileInputStream fsKeys = new FileInputStream(p_certFile);
        fsKeys.read(bytes);
        fsKeys.close();
        bytes = ByteHelper.hexStringToByteArray(new String(bytes));
        _cachedCertificates.put(certName, bytes);
//        System.out.println("CertificatesIO.addItem: Store cert " + certName + " - " + ByteHelper.byteArrayToString(bytes));
        
        // calculate digest
        bytes = calculateDigestFromCertificate(bytes);
        _cachedCertificatesDigest.put(certName, bytes);
//        System.out.println("CertificatesIO.addItem: Store digest: " + ByteHelper.byteArrayToString(bytes) + " - " + certName);
        _cachedReverseCertificatesDigest.put(ByteHelper.byteArrayToString(bytes), certName);
//        System.out.println("CertificatesIO.addItem: Store reverse digest " + ByteHelper.byteArrayToString(bytes) + " - " + certName);
        
        // Load Private Keys
        filename = p_certFile.getPath();
        filename = filename.substring(0, filename.lastIndexOf("."));
        
        try {
               File f = new File(filename+".vkey");
               if(f.exists()){
                   long l = f.length(); 
                   if(l == 32 || l == 64){
                    bytes = new byte[64];
                       fsKeys = new FileInputStream(f);
                       l = fsKeys.read(bytes);
                       fsKeys.close();
                       if(l == 64){
                           bytes = ByteHelper.hexStringToByteArray(new String(bytes));
                           l = 32;
                       }
                       if(l != 32){
                        System.err.println("CertificatesIO: " + f.getName() + ": wrong data length[" + l + "\n");
                       }
                    if (!_cachedSigningPrivateKey.containsKey(certName)) {
                        _cachedSigningPrivateKey.put(certName, bytes);
                    }
                   }else{
                    System.err.println("CertificatesIO: " + f.getName() + ": wrong key file length\n");
                   }
               }
        }catch(FileNotFoundException e){}
        
        try {
               File f = new File(filename+".ekey");
               if(f.exists()){
                   if(f.length() == 32 || f.length() == 64){
                    bytes = new byte[64];
                       fsKeys = new FileInputStream(f);
                       fsKeys.read(bytes);
                       fsKeys.close();
                       if(f.length() == 64){
                           bytes = ByteHelper.hexStringToByteArray(new String(bytes));
                       }
                    if (!_cachedEncryptPrivateKey.containsKey(certName)) {
                        _cachedEncryptPrivateKey.put(certName, bytes);
                    }
                   }
               }
        }catch(FileNotFoundException e){}
    }
    
    @Override
    public String getKeyIdFromHashedId8(byte[] p_hashedId8ToBeUsed) {
        String key = ByteHelper.byteArrayToString(p_hashedId8ToBeUsed);
        if (!_cachedReverseCertificatesDigest.containsKey(key)) {
            return null;
        }
        
        return _cachedReverseCertificatesDigest.get(key).substring(0, _cachedReverseCertificatesDigest.get(key).length() - 7/*.DIGEST*/);
    }
    
    private byte[] calculateDigestFromCertificate(final byte[] p_toBeHashedData) {
        byte[] hash = CryptoLib.hashWithSha256(p_toBeHashedData);
        return ByteHelper.extract(hash, hash.length - 8, 8);
    }
    
} // End of class CertificatesIO
