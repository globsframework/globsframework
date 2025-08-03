package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.exceptions.EOFIOFailure;
import org.globsframework.core.utils.exceptions.InvalidData;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DefaultSerializationInput implements SerializedInput {
    public static final String ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN = "org.globsframwork.serialization.max.len";
    public static final int MAX_SIZE_FOR_BYTES = Integer.getInteger(ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN, 512 * 1024);
    private final InputStream inputStream;
    public int count;

    public DefaultSerializationInput(InputStream inputStream) {
        this.inputStream = inputStream;
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
        BigDecimal[] array = new BigDecimal[length];
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
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("fail to close", e);
        }
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
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        return toInt(ch1, ch2, ch3, ch4);
    }

    public static int toInt(int ch1, int ch2, int ch3, int ch4) {
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFIOFailure("eof");
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0x0));
    }

    private boolean isNull() {
        return read() != 0;
    }

    public Double readDouble() {
        Long l = readLong();
        if (l == null) {
            return null;
        }
        return Double.longBitsToDouble(l);
    }

    public double readNotNullDouble() {
        return Double.longBitsToDouble(readNotNullLong());
    }

    public String readUtf8String() {
        byte[] bytes = readBytes();
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public Boolean readBoolean() {
        int i = read();
        return i == 0 ? Boolean.FALSE : i == 1 ? Boolean.TRUE : null;
    }

    public Long readLong() {
        if (isNull()) {
            return null;
        }
        return readNotNullLong();
    }

    public long readNotNullLong() {
        return (((long) read() << 56) +
                ((long) (read()) << 48) +
                ((long) (read()) << 40) +
                ((long) (read()) << 32) +
                ((long) (read()) << 24) +
                ((long) (read()) << 16) +
                ((long) (read()) << 8) +
                ((read())));
    }

    private int read() {
        try {
            int i = inputStream.read();
            if (i == -1) {
                throw new EOFIOFailure("eof");
            }
            count++;
            return i;
        } catch (IOException e) {
            throw new EOFIOFailure(e);
        }
    }

    public byte readByte() {
        return (byte) (read());
    }

    public byte[] readBytes() {
        try {
            int length = readNotNullInt();
            if (length == -1) {
                return null;
            }
            int readed = 0;
            if (MAX_SIZE_FOR_BYTES != -1 && length > MAX_SIZE_FOR_BYTES) {
                throw new InvalidData("More than " + MAX_SIZE_FOR_BYTES + " bytes to write  (" + length + ") see " + ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN);
            }
            if (length < 0) {
                throw new InvalidData("negative length : " + length);
            }
            byte[] bytes = new byte[length];
            while (readed != length) {
                int readSize = inputStream.read(bytes, readed, length - readed);
                if (readSize == -1) {
                    throw new EOFIOFailure("Missing data in buffer expected " + length + " but was " + readed);
                }
                readed += readSize;
            }
            count += readed;
            return bytes;
        } catch (IOException e) {
            throw new UnexpectedApplicationState(e);
        }
    }

}
