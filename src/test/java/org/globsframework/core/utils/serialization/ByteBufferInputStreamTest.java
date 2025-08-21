package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.NanoChrono;
import org.globsframework.core.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufferInputStreamTest {

    @Test
    public void testReadFile() throws IOException {
        File file = TestUtils.getFileName(this, "sample.dat");
        file.getParentFile().mkdirs();
        file.deleteOnExit();

        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        SerializedOutput output = new DefaultSerializationOutput(outputStream);
        for (int i = 0; i < 100; i++) {
            output.write(3);
            output.write(33L);
            output.writeUtf8String("Some data");
        }
        outputStream.close();
        Path path = file.toPath();

        for (int j = 0; j < 100; j++) {
            ByteBufferInputStream byteBufferInputStream =
                    new ByteBufferInputStream(ByteBuffer.allocateDirect(8 * 1024),
                            Files.newByteChannel(path));
            DefaultSerializationInput defaultSerializationInput = new DefaultSerializationInput(byteBufferInputStream);
//            DefaultSerializationInput defaultSerializationInput = new DefaultSerializationInput(new YANBuffereInputStream(Files.newInputStream(path)));
            NanoChrono nanoChrono = NanoChrono.start();
            for (int i = 0; i < 100; i++) {
                assertEquals(3, defaultSerializationInput.readNotNullInt());
                assertEquals(33L, defaultSerializationInput.readNotNullLong());
                assertEquals("Some data", defaultSerializationInput.readUtf8String());
            }
            System.out.println(nanoChrono.getElapsedTimeInMS() + " ms");
        }
    }
}
