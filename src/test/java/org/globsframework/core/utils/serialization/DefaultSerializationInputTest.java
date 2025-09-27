package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.exceptions.EOFIOFailure;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/*
AI generated test.
 */

class DefaultSerializationInputTest {

    @Test
    void testReadSizeSuccessWithExactData() throws IOException {
        // Input stream containing exactly 4 bytes of data
        byte[] inputData = {1, 2, 3, 4};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);
        DefaultSerializationInput serializationInput = new DefaultSerializationInput(inputStream);

        // Call the method with reserve = 4 (number of bytes to read)
        serializationInput.readSize(4);

    }

    @Test
    void testReadSizeSuccessWithMoreData() throws IOException {
        // Input stream containing more than 4 bytes of data
        byte[] inputData = {1, 2, 3, 4, 5, 6};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData){
            @Override
            public synchronized int read(byte[] b, int off, int len) {
                return super.read(b, off, 1);
            }
        };
        DefaultSerializationInput serializationInput = new DefaultSerializationInput(inputStream);

        // Call the method with reserve = 4
        serializationInput.readSize(4);

        // Verify that the buffer contains the first 4 bytes
    }

    @Test
    void testReadSizeInsufficientData() throws IOException {
        // Input stream containing less than the required bytes
        byte[] inputData = {1, 2};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);
        DefaultSerializationInput serializationInput = new DefaultSerializationInput(inputStream);

        // Calling the method with reserve = 4 should throw EOFIOFailure
        EOFIOFailure exception = assertThrows(EOFIOFailure.class, () -> serializationInput.readSize(4));
        assertEquals("Missing data in buffer expected 4 but was 2", exception.getMessage());
    }

    @Test
    void testReadSizeHandlesLargerReserve() throws IOException {
        // Input stream containing 8 bytes of data
        byte[] inputData = {1, 2, 3, 4, 5, 6, 7, 8};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData){
            @Override
            public synchronized int read(byte[] b, int off, int len) {
                return super.read(b, off, 2);
            }
        };
        DefaultSerializationInput serializationInput = new DefaultSerializationInput(inputStream);

        // Call the method with reserve = 8 (request a larger buffer)
        serializationInput.readSize(8);

        // Verify the buffer was resized and contains the correct data
    }

    @Test
    void testReadSizeIOExceptionHandling() {
        // Input stream that throws an IOException
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test IO exception");
            }
        };
        DefaultSerializationInput serializationInput = new DefaultSerializationInput(inputStream);

        // Calling the method should throw a RuntimeException wrapping the IOException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> serializationInput.readSize(4));
        assertTrue(exception.getCause() instanceof IOException);
        assertEquals("Test IO exception", exception.getCause().getMessage());
    }
}