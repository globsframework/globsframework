package org.globsframework.core.utils.serialization;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class ByteBufferSerializationInput implements SerializedInput {
    private byte[] data;
    private int len;
    private int count;

    public ByteBufferSerializationInput(byte[] data, int len) {
        this.data = data;
        this.len = len;
        if (len > data.length) {
            throw new RuntimeException("Data size too small offset " + 0 + " len " + len + " array size " + data.length);
        }
    }

    public ByteBufferSerializationInput(byte[] data, int offset, int len) {
        this.data = data;
        count = offset;
        if (len - offset > data.length) {
            throw new RuntimeException("Data size too small offset " + offset + " len " + len + " array size " + data.length);
        }
    }

    public void reset(int offset, int len) {
        count = offset;
        this.len = len;
        if (len - offset > data.length) {
            throw new RuntimeException("Data size too small offset " + offset + " len " + len + " array size " + data.length);
        }
    }

    public void reset(byte[] data, int offset, int len) {
        this.data = data;
        count = offset;
        this.len = len;
        if (len - offset > data.length) {
            throw new RuntimeException("Data size too small offset " + offset + " len " + len + " array size " + data.length);
        }
    }

    public int position() {
        return count;
    }

    public int[] readIntArray() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readNotNullInt();
        }
        return array;
    }

    public long[] readLongArray() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        long[] array = new long[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readNotNullLong();
        }
        return array;
    }

    public double[] readDoubleArray() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readNotNullDouble();
        }
        return array;

    }

    public boolean[] readBooleanArray() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        boolean[] array = new boolean[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readBoolean();
        }
        return array;
    }

    public BigDecimal[] readBigDecimalArray() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        BigDecimal array[] = new BigDecimal[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readBigDecimal();
        }
        return array;

    }

    public String[] readStringArray() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        String[] array = new String[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = readUtf8String();
        }
        return array;
    }

    public void close() {
    }

    public BigDecimal readBigDecimal() {
        String s = readUtf8String();
        if (s == null) {
            return null;
        }
        return new BigDecimal(s);
    }


    public LocalDate readDate() {
        int year = readNotNullInt();
        if (year == Integer.MIN_VALUE) {
            return null;
        }
        int month = readNotNullInt();
        int day = readNotNullInt();
        return LocalDate.of(year, month, day);
    }

    public ZonedDateTime readDateTime() {
        int year = readNotNullInt();
        if (year == Integer.MIN_VALUE) {
            return null;
        }
        int month = readNotNullInt();
        int day = readNotNullInt();
        int hour = readNotNullInt();
        int min = readNotNullInt();
        int second = readNotNullInt();
        int nano = readNotNullInt();
        String zone = readUtf8String();
        return ZonedDateTime.of(LocalDate.of(year, month, day),
                LocalTime.of(hour, min, second, nano), ZoneId.of(zone));
    }

    public Integer readInteger() {
        if (isNull()) {
            return null;
        }
        return readNotNullInt();
    }

    public int readNotNullInt() {
        return readI();
    }

    private int readI() {
        int p = count;
        count += 4;
        if (count > len) {
            throw new RuntimeException("EOF reached");
        }
        return (((data[p]) << 24)
                + ((data[p + 1] & 0xff) << 16)
                + ((data[p + 2] & 0xff) << 8)
                + ((data[p + 3] & 0xff) << 0x0));
    }

    private boolean isNull() {
        return read() != 0;
    }

    public Double readDouble() {
        if (isNull()) {
            return null;
        }
        return Double.longBitsToDouble(readL());
    }

    public double readNotNullDouble() {
        return Double.longBitsToDouble(readNotNullLong());
    }

    public String readUtf8String() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        int offset = count;
        count += length;
        if (count > len) {
            throw new RuntimeException("EOF reached " + count + " " + len);
        }
        return new String(data, offset, length, StandardCharsets.UTF_8);
    }

    public Boolean readBoolean() {
        int i = read();
        return i == 0 ? Boolean.FALSE : i == 1 ? Boolean.TRUE : null;
    }

    public Long readLong() {
        if (isNull()) {
            return null;
        }
        return readL();
    }

    public long readNotNullLong() {
        return readL();
    }

    private long readL() {
        int p = count;
        count += 8;
        if (count > len) {
            throw new RuntimeException("EOF reached");
        }
        return (((long) (data[p]) << 56) +
                ((long) (data[p + 1] & 0xff) << 48) +
                ((long) (data[p + 2] & 0xff) << 40) +
                ((long) (data[p + 3] & 0xff) << 32) +
                ((long) (data[p + 4] & 0xff) << 24) +
                ((long) (data[p + 5] & 0xff) << 16) +
                ((long) (data[p + 6] & 0xff) << 8) +
                ((data[p + 7] & 0xff)));
    }

    private int read() {
        int p = count;
        count += 1;
        if (count > len) {
            throw new RuntimeException("EOF reached");
        }
        return data[p] & 0xff;
    }

    public byte readByte() {
        return (byte) read();
    }

    public byte[] readBytes() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        int offset = count;
        count += length;
        if (count > len) {
            throw new RuntimeException("EOF reached");
        }
        return Arrays.copyOfRange(data, offset, count);
    }

}
