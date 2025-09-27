package org.globsframework.core.utils.serialization;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/*
IA generated test (and fixed)
 */

class BufferInputStreamWithLimitTest {

    private static byte[] range(int n) {
        byte[] r = new byte[n];
        for (int i = 0; i < n; i++) r[i] = (byte) i;
        return r;
    }

    @Test
    void readSingleBytesRespectsLimitAndReset() throws IOException {
        byte[] data = range(10);
        BufferInputStreamWithLimit in = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 4);

        // Set a limit smaller than the internal buffer size so count > limit after first fill
        in.limit(3);

        // Read exactly up to limit
        assertEquals(0, in.read());
        assertEquals(1, in.read());
        assertEquals(2, in.read());

        // Next read should hit the limit
        assertThrows(LimitReachedException.class, in::read);

        // After resetLimit, we can continue reading
        in.resetLimit();
        assertEquals(3, in.read());
        assertEquals(4, in.read());
    }

    @Test
    void readArrayWithinLimitAndBeyondLimit() throws IOException {
        byte[] data = range(10);
        BufferInputStreamWithLimit in = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 4);

        in.limit(5);

        byte[] buf = new byte[5];
        int read = in.read(buf, 0, 5);
        assertEquals(5, read);
        assertArrayEquals(new byte[]{0,1,2,3,4}, buf);

        // Any further read within the same limited section should fail
        assertThrows(LimitReachedException.class, () -> in.read(buf, 0, 1));

        // Large request that exceeds the limit should fail immediately
        BufferInputStreamWithLimit in2 = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 4);
        in2.limit(3);
        assertThrows(LimitReachedException.class, () -> in2.read(new byte[4], 0, 4));
    }

    @Test
    void readToLimitReturnsFalseWhenItHadToConsumeBytes() throws IOException {
        byte[] data = range(10);
        // Buffer size 4; set limit 3 so the first fill will have count=4 and currentPos will advance to limit
        BufferInputStreamWithLimit in = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 4);
        in.limit(3);

        boolean done = in.readToLimit();
        assertFalse(done, "Should return false because it consumed bytes to reach the limit");
        assertEquals(3, in.read()); // next byte after 0,1,2
    }

    @Test
    void readToLimitReturnsTrueAndResetsWhenLimitEqualsCount() throws IOException {
        byte[] data = range(10);
        // Use buffer size 3 so that after first fill, count == 3
        BufferInputStreamWithLimit in = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 3);
        in.limit(3); // limit equals buffer size

        // Limit was reset by readToLimit; we can freely read
        assertEquals(0, in.read());
        assertEquals(1, in.read());
        assertEquals(2, in.read());

        // Here, since limit == count (3), readToLimit should directly reset and return true
        boolean done = in.readToLimit();
        assertTrue(done, "Should return true because limit equals internal count");

    }

    @Test
    void endOfStreamWithoutLimit() throws IOException {
        byte[] data = range(3);
        BufferInputStreamWithLimit in = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 2);

        assertEquals(0, in.read());
        assertEquals(1, in.read());
        assertEquals(2, in.read());
        assertEquals(-1, in.read());
    }

    @Test
    void adjustLimitOnArrayReadAfterExhaust() throws IOException {
        byte[] data = range(12);
        BufferInputStreamWithLimit in = new BufferInputStreamWithLimit(new ByteArrayInputStream(data), 4);
        in.limit(8);

        // Consume exactly one full internal buffer using single-byte reads
        assertEquals(0, in.read());
        assertEquals(1, in.read());
        assertEquals(2, in.read());
        assertEquals(3, in.read());

        // Now currentPos == count; the next array read will trigger the branch at line 73
        byte[] buf = new byte[3];
        int r = in.read(buf, 0, 3);
        assertEquals(3, r);
        assertArrayEquals(new byte[]{4, 5, 6}, buf);

        // We had limit 8, consumed 7 so far; one more byte should be allowed
        assertEquals(7, in.read());

        // Any further read (even 1 byte) must hit the limit due to the adjusted limit at line 73
        assertThrows(LimitReachedException.class, () -> in.read(new byte[1], 0, 1));
    }
}
