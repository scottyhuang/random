package com.scotth.random;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;

/**
 * DevRandom simulates linux /dev/random
 * Depends on Apache Commons Crypto package 
 * https://commons.apache.org/proper/commons-crypto/
 * Default implementation is the procesor's hardware Digital Random Number Generator 
 * (DRNG). 
 * Backup implementation is Java SecureRandom cryptographically 
 * strong RNG.
 * 
 */
public class DevRandom {
    private static final String DEFAULT_FILE_PATH = "devrandom";
    private static final int BUFFER_SIZE = 4;
  
    private static CryptoRandom random = null;

    static {
        initializeRandom();
    }

    public static void initializeRandom() {
        try {
            DevRandom.random = CryptoRandomFactory.getCryptoRandom();
        } catch (GeneralSecurityException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void writeNextRandomBytes(
        CryptoRandom random, OutputStream out, byte[] b, int off, int len) 
            throws IOException{
        random.nextBytes(b);
        out.write(b);
    }

    /**
    * Writes random bytes to default output file continuously
    * @throws GeneralSecurityException if CryptoRandom is not created
    * @throws IOException if IO errors
    */
    public static void writeRandomBytes() 
        throws IOException, GeneralSecurityException {
            writeRandomBytes(DevRandom.DEFAULT_FILE_PATH, -1);
    }

    /**
    * Writes Random Bytes to output file continuously
    * @param   path    - path of the output file
    * @throws GeneralSecurityException if CryptoRandom is not created
    * @throws IOException if IO errors
    */
    public static void writeRandomBytes(String path) 
        throws IOException, GeneralSecurityException {
            writeRandomBytes(path, -1);
    }

    /**
    * @param   path    - path of the output file
    * @param   size    - size of the output file in bytes
    * @throws GeneralSecurityException if CryptoRandom is not created
    * @throws IOException if IO errors
    */
    public static void writeRandomBytes(String path, int size) throws 
        IOException, GeneralSecurityException {
        if (random == null) {
            throw new GeneralSecurityException("CryptoRandom Object Not Found");
        }

        if (path == null || path.length() < 1) {
            path = DevRandom.DEFAULT_FILE_PATH;
        }
        
        Path file = Paths.get(path);
        OutputStream out = null;
        
        byte[] b = new byte[DevRandom.BUFFER_SIZE];

        try {
            out = Files.newOutputStream(file);
            
            if (size < 0) {
                while(true) {
                    writeNextRandomBytes(random, out, b, 0, DevRandom.BUFFER_SIZE);
                }
            } 

            for (int i=0; i < size; i += DevRandom.BUFFER_SIZE) { 
                int length = size - i < DevRandom.BUFFER_SIZE ? size -i : DevRandom.BUFFER_SIZE;
                writeNextRandomBytes(random, out, b, 0, length);
            }
           
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
    * @param -f,--filepath   output file path
    * @param -s,--size       output file size in bytes
    */
    public static void main(final String []args) 
        throws IOException, GeneralSecurityException{
        Options options = new Options();
        options.addOption("f", "filepath", false, "output file path")
               .addOption("s", "size", false, "output file size in bytes");
                  
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DevRandom", options);

        CommandLineParser parser = new DefaultParser();
        String path = null;
        int size = -1;

        try {
            CommandLine cmd = parser.parse( options, args);
            if (cmd.hasOption("f")) {
                path = cmd.getOptionValue("f");
            }
            if (cmd.hasOption("s")) {
                String sizeStr = cmd.getOptionValue("s");
                try {
                    if (sizeStr != null) {
                        size = Integer.parseInt(sizeStr);
                    }
                } catch (NumberFormatException e) {
                    size = 0;
                }
            }
        } catch (ParseException e) {
            System.err.println("Error encountered Parsing Arguments. " + e.getMessage());
            return;
        }
        
        writeRandomBytes(path, size);
    }
}
