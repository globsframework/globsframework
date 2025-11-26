package org.globsframework.core.utils.serialization;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IA generated test
 * Tests focusing on InputStream methods read() and read(byte[], off, len)
 * of NByteBufferSerializationInput, including cross-buffer refills and limits.
 */
public class NByteBufferSerializationInputReadMethodsTest {

    private static class ChunkedProvider implements NByteBufferSerializationInput.NextBuffer {
        private final byte[] all;
        private final int chunkSize;
        private int offset;

        ChunkedProvider(byte[] all, int chunkSize) {
            this.all = all;
            this.chunkSize = chunkSize;
            this.offset = 0;
        }

        ByteBuffer first() {
            int len = Math.min(chunkSize, all.length);
            offset = len;
            return ByteBuffer.wrap(all, 0, len);
        }

        @Override
        public ByteBuffer refill(ByteBuffer prev) {
            if (offset >= all.length) {
                return ByteBuffer.wrap(new byte[0]);
            }
            int len = Math.min(chunkSize, all.length - offset);
            ByteBuffer next = ByteBuffer.wrap(all, offset, len);
            offset += len;
            return next;
        }
    }

    private static byte[] buildBytes(java.util.function.Consumer<ByteBufferSerializationOutput> writer) {
        byte[] buffer = new byte[64 * 1024];
        ByteBufferSerializationOutput out = new ByteBufferSerializationOutput(buffer);
        writer.accept(out);
        return Arrays.copyOf(buffer, out.position());
    }

    private static NByteBufferSerializationInput chunkedInput(byte[] all, int chunkSize) {
        ChunkedProvider provider = new ChunkedProvider(all, chunkSize);
        return new NByteBufferSerializationInput(provider.first(), provider);
    }

    @Test
    public void testReadSingleBytesAcrossRefills() throws Exception {
        // Create predictable sequence 0..255 twice, so 512 bytes
        byte[] all = new byte[512];
        for (int i = 0; i < all.length; i++) all[i] = (byte) (i & 0xFF);

        // Force tiny chunks to exercise refill path frequently
        NByteBufferSerializationInput in = chunkedInput(all, 3);

        for (int i = 0; i < all.length; i++) {
            int r = in.read();
            assertTrue(r >= 0, "Expected a byte before EOF at index " + i);
            assertEquals(all[i] & 0xFF, r, "Mismatch at index " + i);
        }
        // After all consumed, subsequent reads must return -1
        assertEquals(-1, in.read());
        assertEquals(-1, in.read());
    }

    @Test
    public void testBulkReadWithOffsetAndLength() throws Exception {
        // Prepare 100 bytes 0..99
        byte[] all = new byte[100];
        for (int i = 0; i < all.length; i++) all[i] = (byte) i;

        NByteBufferSerializationInput in = chunkedInput(all, 7);

        byte[] dst = new byte[150];
        Arrays.fill(dst, (byte) 0x55);

        // zero-length read should return 0 and not modify buffer
        assertEquals(0, in.read(dst, 10, 0));
        for (byte b : dst) assertEquals((byte) 0x55, b);

        // Read first 30 bytes into dst[5..34]
        int n1 = in.readNBytes(dst, 5, 30);
        assertEquals(30, n1);
        for (int i = 0; i < 30; i++) {
            assertEquals((byte) i, dst[5 + i], "bulk read mismatch at " + i);
        }

        // Next read 60 bytes into dst[40..99] (crosses many chunk refills)
        int n2 = in.readNBytes(dst, 40, 60);
        assertEquals(60, n2);
        for (int i = 0; i < 60; i++) {
            assertEquals((byte) (30 + i), dst[40 + i], "bulk read mismatch at second block index " + i);
        }

        // Only 10 bytes left, request 20 -> should return 10
        int n3 = in.readNBytes(dst, 100, 20);
        assertEquals(10, n3);
        for (int i = 0; i < 10; i++) {
            assertEquals((byte) (90 + i), dst[100 + i], "last bytes mismatch at " + i);
        }

        // Now EOF
        assertEquals(-1, in.read(dst, 0, 50));
    }

    @Test
    public void testReadRespectsLimit() throws Exception {
        // 32 bytes
        byte[] all = new byte[32];
        for (int i = 0; i < all.length; i++) all[i] = (byte) (i + 1);
        NByteBufferSerializationInput in = chunkedInput(all, 5);

        // Limit to 10 bytes and read via bulk
        in.limit(10);
        byte[] dst = new byte[20];
        int r1 = in.readNBytes(dst, 0, 20);
        assertEquals(10, r1);
        for (int i = 0; i < 10; i++) assertEquals((byte) (i + 1), dst[i]);
        assertEquals(-1, in.read(dst, 0, 5));

        // Reset limit and read single bytes for the remaining 22
        in.resetLimit();
        int count = 0;
        int v;
        while ((v = in.read()) != -1) {
            count++;
        }
        assertEquals(22, count);
    }
}
