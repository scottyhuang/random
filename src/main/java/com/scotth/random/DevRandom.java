package com.scotth.random;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;

/**
 * DevRandom clones /dev/random
 * Using Apache Commons Crypto 
 *
 */
class DevRandom {
    static final Properties properties;
    static String fileName = "dev-random";
    static final int offSet = 4;

    static {
        properties = new Properties();
        properties.put(CryptoRandomFactory.CLASSES_KEY,
            CryptoRandomFactory.RandomProvider.OPENSSL.getClassName());
    }

    static CryptoRandom random = initializeRandom();

    private static CryptoRandom initializeRandom() {
        try {
            return CryptoRandomFactory.getCryptoRandom(properties);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }
   
    /**
    * @param   path    - the path to the file
    * @param   size    - an object to iterate over the char sequences
    * truncate and overwrite an existing file, or create the file if
    * it doesn't initially exist
    */
    public static void writeRandomBytes(String path, int size) throws IOException{
        if (random == null) {
            random = initializeRandom();
        }

        Path file = Paths.get(path);
        OutputStream out = null;
        int step = offSet;
        byte[] b = new byte[offSet];

        try {
            out = Files.newOutputStream(file);
            int i;
            
            if (size < 0) {
                while(true) {
                    random.nextBytes(b);
                    out.write(b);  
                }
            } 

            for (i=0; i < size - offSet; i+=step) {    
                random.nextBytes(b);
                out.write(b);
            }
            if (i < size) {
                random.nextBytes(b);
                out.write(b, 0, size - i);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void main(final String []args) throws IOException {
       writeRandomBytes("./dev/random", -1);
    }
}