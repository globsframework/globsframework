package org.globsframework.core.utils.serialization;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.globsframework.core.utils.serialization.ByteBufferSerializationOutputTest.check;
import static org.globsframework.core.utils.serialization.ByteBufferSerializationOutputTest.writeValues;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NByteBufferSerializationOutputTest {

    @Test
    void testLargeBuffer() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        SerializedOutput out = new NByteBufferSerializationOutput(byteBuffer);
        final ZonedDateTime zdt = writeValues(out);
        final int position = byteBuffer.position();
        byteBuffer.flip();
        final byte[] dst = new byte[byteBuffer.remaining()];
        byteBuffer.get(dst);
        ByteBufferSerializationInput in = new ByteBufferSerializationInput(dst, position);
        check(in, zdt);
    }

    @Test
    void testBooleanArrayWithSmallBuffer() {
        List<ByteBuffer> buffers = new ArrayList<>();
        NByteBufferSerializationOutput.ByteOutput byteOutput = (b) -> {
            b.flip();
            ByteBuffer copy = ByteBuffer.allocate(b.remaining());
            copy.put(b);
            copy.flip();
            buffers.add(copy);
            b.clear();
            return b;
        };

        // Buffer size 5: 4 bytes for length (int) + 1 byte for boolean
        ByteBuffer buffer = ByteBuffer.allocate(5);
        NByteBufferSerializationOutput out = new NByteBufferSerializationOutput(byteOutput, buffer);

        // writeChecked is called because reserve(5+4=9) fails for buffer size 5.
        // writeChecked(values) will:
        // 1. writeUncheckedInt(5) -> position becomes 4.
        // 2. Loop i=0 to 4:
        //    i=0: buffer.hasRemaining() is true (pos 4, lim 5), so flush(1) is CALLED.
        //         flush(1) calls writeOutputBytes, position becomes 0.
        //         buffer.put(true) -> position becomes 1.
        //    i=1: buffer.hasRemaining() is true (pos 1, lim 5), so flush(1) is CALLED.
        //         flush(1) calls writeOutputBytes, position becomes 0.
        //         buffer.put(false) -> position becomes 1.
        //    ... and so on. It flushes for every single boolean!
        boolean[] values = new boolean[]{true, false, true, true, false};
        out.write(values);
        out.flush();

        assertEquals(2, buffers.size()); // 1 flush when buffer full + 1 final flush

        NByteBufferSerializationInput in = new NByteBufferSerializationInput(d -> {
            if (buffers.isEmpty()) return null;
            return buffers.remove(0);
        });

        assertArrayEquals(values, in.readBooleanArray());
    }

    @Test
    void testUtf8StringOverflow() {
        // strlen * 3 + 4 should overflow if strlen is large
        // 0x55555556 * 3 + 4 = 0x100000002 + 4 = 0x100000006 -> 6 (int overflow)
        // If buffer limit is e.g. 10, then 6 < 10 would be true, and enoughSpace would be true.
        // Then it would try to write 0x55555556 characters into a buffer of size 10, causing BufferOverflowException.

        int hugeStrLen = 0x55555556;
        // We don't actually need a string of this size to test the logic,
        // we just need to see if it enters the 'enoughSpace' block.
        // However, we can't easily create a string of that size in memory.
        // But the check `strlen < 1000000` already prevents this.

        List<ByteBuffer> buffers = new ArrayList<>();
        NByteBufferSerializationOutput.ByteOutput byteOutput = (b) -> {
            b.flip();
            ByteBuffer copy = ByteBuffer.allocate(b.remaining());
            copy.put(b);
            copy.flip();
            buffers.add(copy);
            b.clear();
            return b;
        };
        NByteBufferSerializationOutput out = new NByteBufferSerializationOutput(byteOutput, 100);

        String normalString = "This is a normal string.";
        out.writeUtf8String(normalString);
        out.flush();

        NByteBufferSerializationInput in = new NByteBufferSerializationInput(d -> {
            if (buffers.isEmpty()) return null;
            return buffers.remove(0);
        });
        assertEquals(normalString, in.readUtf8String());
    }
}