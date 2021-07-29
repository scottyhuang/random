package com.scotth.random;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit test for DevRandom
 */
public class DevRandomTest 
{
    private static final String DEFAULT_FILE_PATH = "./random-test";

    private static long getFileLength(String filePath) throws IOException {
        File file = new File(filePath);
        return file.length();
    }

    /**
     * Test DevRandom.writeRandomBytes()
     * using parameterized output file size
     * and default output file path
     *
     */
    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4, 7, 128, 133, 512, 514 })
    public void testWriteRandomBytesWithSizeArgument(int argument)
    {
        try{
            DevRandom.writeRandomBytes(DevRandomTest.DEFAULT_FILE_PATH, argument);
            long fileSize = getFileLength(DevRandomTest.DEFAULT_FILE_PATH);
            assertEquals(fileSize, argument);
        } catch (Exception e) {
            assertEquals("exception", e.getMessage());
        }
    }

    @AfterAll
    public static void performOneTimeTeardown() {
        File file = new File(DevRandomTest.DEFAULT_FILE_PATH);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            System.err.println("Unable to cleanup test file. " + e.getMessage());
        }
    }    
}
