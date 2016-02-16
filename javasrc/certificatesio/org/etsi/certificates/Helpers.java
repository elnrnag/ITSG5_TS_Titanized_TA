/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/certificatesio/org/etsi/certificates/Helpers.java $
 *              $Id:
 */
package org.etsi.certificates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Helpers {


    /** 
     * A single instance of this class
     */
    private static Helpers instance;
    
    /** 
     * Synchronization object to provide single access to the instance
     */
    private static Object _sync = new Object();

    /**
     * Provide the access to a single instance of this class
     * @return    A single instance of this class
     */
    public static Helpers getInstance() {
        if (instance == null) {
            synchronized(_sync) {
                if (instance == null) {
                    instance = new Helpers();
                }
            }
        }

        return instance;
    }

    /**
     * Internal ctor
     */
    private Helpers() {
    }

    /**
     * Convert an array of strings to one string
     * @param list string list
     * @param separator 'separator' string between each element
     * @return string list items concatenated into a string
     */
    public String arrayToString(String[] list, String separator) {
        StringBuffer result = new StringBuffer();
        if (list.length > 0) {
            result.append(list[0]);
            for (int i = 1; i < list.length; i++) {
                result.append(separator);
                result.append(list[i]);
            }
        }
        
        return result.toString();
    }

    /**
     * Recursively walk a directory tree and return a List of all Files found; the List is sorted using File.compareTo().
     * @param p_startingDir The valid directory, which can be read.
     * @param p_extension The file extension, in lower case
     * @param p_excludedPatterns The pattern which shall be excluded, in lower case
     */
    public List<File> getFileListing(File p_startingDir, final String p_extension, final String[] p_excludedPatterns) throws FileNotFoundException {
        validateDirectory(p_startingDir);
        List<File> result = getFileListingNoSort(p_startingDir, p_extension, p_excludedPatterns);
        Collections.sort(result);
        return result;
    }

    private List<File> getFileListingNoSort(final File p_startingDir, final String p_extension, final String[] p_excludedPatterns) throws FileNotFoundException {
        List<File> result = new ArrayList<File>();
        FilenameFilter filter = new FilenameFilter() {
            
            @Override
            public boolean accept(final File p_dirName, final String p_fileName) {
                String name = p_fileName.toLowerCase();
//                System.out.println("getFileListingNoSort: " + name + " - " + p_extension + " - " + name.endsWith(p_extension));
                if (!p_extension.isEmpty() && !name.endsWith(p_extension)) {
                    return false;
                }
                
                if (p_excludedPatterns != null) {
                    for (String excludePattern : p_excludedPatterns) {
                        if (name.indexOf(excludePattern) != -1) {
//                            System.out.println("getFileListingNoSort: exclusion criteria=" + excludePattern);
                            return false;
                        }
                    }
                }
                return true;
            }
        };
        File[] filesAndDirs = p_startingDir.listFiles(filter);
        List<File> filesDirs = Arrays.asList(filesAndDirs);
        for (File file : filesDirs) {
            result.add(file); // always add, even if directory
            if (!file.isFile()) {
                //must be a directory
                //recursive call!
                List<File> deeperList = getFileListingNoSort(file, p_extension, p_excludedPatterns);
                result.addAll(deeperList);
            }
        }
        return result;
    }
    
    /**
     * Directory is valid if it exists, does not represent a file, and can be read.
     */
    private void validateDirectory (File aDirectory) throws FileNotFoundException {
        if (aDirectory == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!aDirectory.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + aDirectory);
        }
        if (!aDirectory.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + aDirectory);
        }
        if (!aDirectory.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
        }
    } 
    
} // End of class Helpers
