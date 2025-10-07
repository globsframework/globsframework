package org.globsframework.core.utils.serialization;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class CompressedSerializationTest {

    @Test
    void testLotOfStr() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressedSerializationOutput compressedSerializationOutput = new CompressedSerializationOutput(outputStream);

        for (int i = 0; i < 10000; i++) {
            compressedSerializationOutput.writeUtf8String(String.valueOf(i));
        }

        CompressedSerializationInput compressedSerializationInput = new CompressedSerializationInput(
                new ByteArrayInputStream(outputStream.toByteArray()));

        for (int i = 0; i < 10000; i++) {
            assertEquals(String.valueOf(i), compressedSerializationInput.readUtf8String());
        }
    }

    @Test
    void testDuplicateStr() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressedSerializationOutput compressedSerializationOutput = new CompressedSerializationOutput(outputStream);

        for (int i = 0; i < 10000; i++) {
            compressedSerializationOutput.writeUtf8String(String.valueOf(i % 100));
        }

        CompressedSerializationInput compressedSerializationInput = new CompressedSerializationInput(
                new ByteArrayInputStream(outputStream.toByteArray()));

        for (int i = 0; i < 10000; i++) {
            assertEquals(String.valueOf(i % 100), compressedSerializationInput.readUtf8String());
        }
    }
}