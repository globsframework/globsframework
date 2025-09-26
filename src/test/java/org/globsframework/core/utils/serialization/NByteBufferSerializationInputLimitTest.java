package org.globsframework.core.utils.serialization;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/*
IA generate test.
 */

public class NByteBufferSerializationInputLimitTest {

    private static ByteBuffer buildBuffer(java.util.function.Consumer<ByteBufferSerializationOutput> writer) {
        byte[] buffer = new byte[4096];
        ByteBufferSerializationOutput out = new ByteBufferSerializationOutput(buffer);
        writer.accept(out);
        int size = out.position();
        return ByteBuffer.wrap(buffer, 0, size);
    }

    private static byte[] buildBytes(java.util.function.Consumer<ByteBufferSerializationOutput> writer) {
        byte[] buffer = new byte[64 * 1024];
        ByteBufferSerializationOutput out = new ByteBufferSerializationOutput(buffer);
        writer.accept(out);
        return Arrays.copyOf(buffer, out.position());
    }

    private static NByteBufferSerializationInput buildNoRefillInput(ByteBuffer byteBuffer) {
        return new NByteBufferSerializationInput(byteBuffer, data -> ByteBuffer.wrap(new byte[0]));
    }

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

    private static NByteBufferSerializationInput buildInputWithChunks(byte[] all, int chunkSize) {
        ChunkedProvider provider = new ChunkedProvider(all, chunkSize);
        return new NByteBufferSerializationInput(provider.first(), provider);
    }

    @Test
    public void testLimit_NoRefill_WithinThenExceed() {
        // Data: 4 (int) + 1 (nullable int null marker) + 5 (nullable int value) + 8 (long) + 1 (byte)
        ByteBuffer buf = buildBuffer(out -> {
            out.write(0x01020304);
            out.writeInteger(null);
            out.writeInteger(42);
            out.write(0x0102030405060708L);
            out.writeByte((byte) 0x7F);
        });

        NByteBufferSerializationInput in = buildNoRefillInput(buf);

        // Limit set to allow reads up to and including the long (total 18 bytes)
        in.limit(18);

        assertEquals(0x01020304, in.readNotNullInt()); // 4
        assertNull(in.readInteger()); // +1 => 5
        assertEquals(42, in.readInteger()); // +5 => 10
        assertEquals(0x0102030405060708L, in.readNotNullLong()); // +8 => 18

        // Next read should exceed the limit
        assertThrows(LimitReachedException.class, in::readByte);
    }

    @Test
    public void testLimit_WithRefill_ExceedOnNextValue() {
        // Sequence: int (4) + long (8) + double (8) + byte (1)
        byte[] all = buildBytes(out -> {
            out.write(0x89ABCDEF);
            out.write(0x0102030405060708L);
            out.write(Math.E);
            out.writeByte((byte) 0x55);
        });

        // Small chunk size to ensure refills happen
        NByteBufferSerializationInput in = buildInputWithChunks(all, 3);

        // Allow exactly int + long (4 + 8 = 12)
        in.limit(12);

        assertEquals(0x89ABCDEF, in.readNotNullInt()); // 4
        assertEquals(0x0102030405060708L, in.readNotNullLong()); // +8 => 12

        // Next read (double) requires 8 bytes and should exceed the limit immediately
        assertThrows(LimitReachedException.class, in::readNotNullDouble);
        in.resetLimit();
    }

    @Test
    public void testLimit_ResetCounterBetweenOperations() {
        // Write: int (4), nullable int null (1), byte (1)
        ByteBuffer buf = buildBuffer(out -> {
            out.write(123456789);
            out.writeInteger(null);
            out.writeByte((byte) 0x10);
        });
        NByteBufferSerializationInput in = buildNoRefillInput(buf);

        // First, limit to 4 to read only the int
        in.limit(4);
        assertEquals(123456789, in.readNotNullInt());
        // Any further read now should exceed
        assertThrows(LimitReachedException.class, in::readInteger);

        // Reset limit to 1 to read the null marker of the nullable int
        in.limit(1);
        assertNull(in.readInteger());

        // Reset limit to 1 to allow reading the trailing byte
        in.resetLimit();
        assertEquals((byte) 0x10, in.readByte());
    }

    @Test
    public void testResetLimit_AllowsFurtherReads() {
        byte[] all = buildBytes(out -> {
            out.write(0x11223344);
            out.write(0x0102030405060708L);
            out.writeByte((byte) 0x42);
        });
        // Small chunks to ensure we cross boundaries
        NByteBufferSerializationInput in = buildInputWithChunks(all, 4);

        // Only allow reading the first int
        in.limit(4);
        assertEquals(0x11223344, in.readNotNullInt());
        assertThrows(LimitReachedException.class, in::readNotNullLong);

        // After resetLimit, we should be able to continue reading normally
        in.resetLimit();
        assertEquals(0x0102030405060708L, in.readNotNullLong());
        assertEquals((byte) 0x42, in.readByte());
    }

    @Test
    public void testReadToLimit_BehaviorAndRefill() {
        // Case 1: With an active limit, readToLimit consumes exactly up to the limit
        ByteBuffer buf = buildBuffer(out -> {
            out.write(0xAABBCCDD);
            out.write(0x1122334455667788L);
            out.writeByte((byte) 0x7F);
        });
        NByteBufferSerializationInput in = buildNoRefillInput(buf);

        in.limit(4); // exactly the int size
        in.readToLimit(); // should consume 4 bytes
        assertThrows(LimitReachedException.class, in::readByte); // at limit

        in.limit(8); // allow the long
        assertEquals(0x1122334455667788L, in.readNotNullLong());
        in.limit(1);
        assertEquals((byte) 0x7F, in.readByte());

        // Case 2: When no limit is set, readToLimit should be a no-op
        ByteBuffer buf2 = buildBuffer(out -> {
            out.write(0x01020304);
            out.writeByte((byte) 0x33);
        });
        NByteBufferSerializationInput in2 = buildNoRefillInput(buf2);
        in2.readToLimit(); // no limit set -> should do nothing
        assertEquals(0x01020304, in2.readNotNullInt());
        assertEquals((byte) 0x33, in2.readByte());

        // Case 3: readToLimit across refill boundaries
        byte[] all = buildBytes(out -> {
            out.writeByte((byte) 0xAB);
            out.write(0xCAFEBABE); // 4 bytes after the first
        });
        NByteBufferSerializationInput in3 = buildInputWithChunks(all, 2);
        in3.limit(1); // only the first byte is allowed
        in3.readToLimit(); // should consume that single byte, using read() and handling refill if needed
        assertThrows(LimitReachedException.class, in3::readNotNullInt);
        in3.resetLimit();
        assertEquals(0xCAFEBABE, in3.readNotNullInt());
    }
}
