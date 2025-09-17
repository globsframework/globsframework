package org.globsframework.core.utils.serialization;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ByteBufferSerializationOutputTest {

    @Test
    void testLargeBuffer() {
        final byte[] buffer = new byte[1024 * 1024];
        ByteBufferSerializationOutput out = new ByteBufferSerializationOutput(buffer);
        final ZonedDateTime zdt = writeValues(out);
        final int position = out.position();
        ByteBufferSerializationInput in = new ByteBufferSerializationInput(buffer, position);
        check(in, zdt);
    }

    @Test
    public void roundTripWithSmallBuffer_ByteOutput() {
        // Capture bytes written by the buffered output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBufferSerializationOutput.ByteOutput sink = (bytes, len) -> baos.write(bytes, 0, len);

        // Use a very small buffer to force multiple flushes during writes
        ByteBufferSerializationOutput out = new ByteBufferSerializationOutput(sink, 16);

        // Write a variety of values (keep byte[] small to avoid the writeBytes large-else path)
        final ZonedDateTime zdt = writeValues(out);

        out.flush();
        byte[] data = baos.toByteArray();
        assertTrue(data.length > 0);

        // Read back with ByteBufferSerializationInput which understands the same encoding
        ByteBufferSerializationInput in = new ByteBufferSerializationInput(data, data.length);
        check(in, zdt);
        // Ensure we've consumed all data
        assertEquals(data.length, in.position());

        DefaultSerializationInput inDefault = new DefaultSerializationInput(new ByteArrayInputStream(data));
        check(inDefault, zdt);

    }

    private static ZonedDateTime writeValues(ByteBufferSerializationOutput out) {
        out.write(3);
        out.write(-3);
        out.write(Integer.MAX_VALUE);
        out.write(Integer.MIN_VALUE);
        out.write(33L);
        out.write(-33L);
        out.write(Long.MAX_VALUE);
        out.write(Long.MIN_VALUE);
        out.write(6.33);
        out.write(false);
        out.write(new int[]{3, 5, 9});
        out.write(new long[]{1L, 2L});
        out.write(new double[]{1.5, -2.25});
        out.writeBoolean(Boolean.TRUE);
        out.writeBoolean(null);
        out.writeInteger(42);
        out.writeInteger(null);
        out.writeLong(123456789L);
        out.writeLong(null);
        out.writeDouble(-123.5);
        out.writeDouble(null);
        out.writeUtf8String("blah");
        out.writeUtf8String("blah blah blah blah blah blah blah");
        out.writeUtf8String("");
        out.writeUtf8String(null);
        out.write(new BigDecimal("123.45"));
        out.write(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("-2.5")});
        out.write(new String[]{"a", null, "é", "€"});
        out.write(new boolean[]{true, false, true});
        out.writeDate(LocalDate.of(2020, 2, 29));
        ZonedDateTime zdt = ZonedDateTime.of(2021, 3, 14, 1, 59, 26, 123456789, ZoneId.of("UTC"));
        out.writeDateTime(zdt);
        return zdt;
    }

    private static void check(SerializedInput in, ZonedDateTime zdt) {
        assertEquals(3, in.readNotNullInt());
        assertEquals(-3, in.readNotNullInt());
        assertEquals(Integer.MAX_VALUE, in.readNotNullInt());
        assertEquals(Integer.MIN_VALUE, in.readNotNullInt());
        assertEquals(33L, in.readNotNullLong());
        assertEquals(-33L, in.readNotNullLong());
        assertEquals(Long.MAX_VALUE, in.readNotNullLong());
        assertEquals(Long.MIN_VALUE, in.readNotNullLong());
        assertEquals(6.33, in.readNotNullDouble(), 0.000001);
        assertEquals(false, in.readBoolean());
        assertArrayEquals(new int[]{3, 5, 9}, in.readIntArray());
        assertArrayEquals(new long[]{1L, 2L}, in.readLongArray());
        assertArrayEquals(new double[]{1.5, -2.25}, in.readDoubleArray(), 0.000001);
        assertEquals(Boolean.TRUE, in.readBoolean());
        assertNull(in.readBoolean());
        assertEquals(42, in.readInteger());
        assertNull(in.readInteger());
        assertEquals(123456789L, in.readLong());
        assertNull(in.readLong());
        assertEquals(-123.5, in.readDouble(), 0.000001);
        assertNull(in.readDouble());
        assertEquals("blah", in.readUtf8String());
        assertEquals("blah blah blah blah blah blah blah", in.readUtf8String());
        assertEquals("", in.readUtf8String());
        assertNull(in.readUtf8String());
        assertEquals(new BigDecimal("123.45"), in.readBigDecimal());
        assertArrayEquals(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("-2.5")}, in.readBigDecimalArray());
        assertArrayEquals(new String[]{"a", null, "é", "€"}, in.readStringArray());
        assertArrayEquals(new boolean[]{true, false, true}, in.readBooleanArray());
        assertEquals(LocalDate.of(2020, 2, 29), in.readDate());
        assertEquals(zdt, in.readDateTime());
    }

    @Test
    public void flushAtBoundary_equalsDefaultSerializationOutput() {
        // Prepare two outputs writing the same ints using different implementations
        ByteArrayOutputStream baosBuffered = new ByteArrayOutputStream();
        ByteBufferSerializationOutput buffered = new ByteBufferSerializationOutput((bytes, len) -> baosBuffered.write(bytes, 0, len), 4);

        ByteArrayOutputStream baosDefault = new ByteArrayOutputStream();
        DefaultSerializationOutput defaultOut = new DefaultSerializationOutput(new DefaultSerializationOutput.ByteOutput() {
            @Override
            public void writeOutputByte(int b) {
                baosDefault.write(b);
            }

            @Override
            public void writeOutputBytes(byte[] b, int len) {
                baosDefault.write(b, 0, len);
            }

        });

        // Write two ints. With buffer size 4, each write fills the buffer and triggers a flush between them
        buffered.write(1);
        buffered.write(2);
        buffered.flush();

        defaultOut.write(1);
        defaultOut.write(2);

        assertArrayEquals(baosDefault.toByteArray(), baosBuffered.toByteArray());
    }
}
