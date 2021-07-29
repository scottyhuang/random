
package com.scotth.random;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to generate random seed from system sources
 */
public class CustomSeed {
    
    public static final String DEFAULT_ALGORITHM = "SHA-256";

    //Utility method for extracting the LSB of a long value
    public static long getLSB(long longValue){
         return (longValue & 0x00000000FFFFFFFF);
    }

    public static byte[] concatByteArrays(byte[] a, byte[] b) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);
        
        return outputStream.toByteArray();
    }

    /**
     * Method to calculate the Cryptographic hash value
     * @param input - input byte array
     * @param algorithm - hasing algorithm
    */
    public static byte[] getCryptoHash(byte[] input, String algorithm) 
        throws NoSuchAlgorithmException{
        
        MessageDigest msgDigest = MessageDigest.getInstance(algorithm);
        BigInteger inputDigestBigInt = new BigInteger(1, msgDigest.digest(input));

        return inputDigestBigInt.toByteArray();
    }

    /**
     * Method to get seed from system entropy sources
    */
    public static byte[] getSeed() throws NoSuchAlgorithmException, IOException{
        BigInteger nowInMilliBigInt = BigInteger.valueOf(System.currentTimeMillis());
        byte[] h1 = getCryptoHash(nowInMilliBigInt.toByteArray(), DEFAULT_ALGORITHM);

        MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        BigInteger memUsageBigInt = BigInteger.valueOf(memUsage.getUsed());
        byte[] h2 = getCryptoHash(memUsageBigInt.toByteArray(), DEFAULT_ALGORITHM);

        return getCryptoHash(concatByteArrays(h1, h2), DEFAULT_ALGORITHM);
    }        

    public static void main(String args[]) {
        try{
            System.out.println("Entropy: " + getSeed().toString());
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Error during Crypto Hash generation. " + e.getMessage());
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}