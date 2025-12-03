package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.exceptions.EOFIOFailure;
import org.globsframework.core.utils.exceptions.InvalidData;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DefaultSerializationInput implements SerializedInput {
    public static final String ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN = "org.globsframwork.serialization.max.len";
    public static final int MAX_SIZE_FOR_BYTES = Integer.getInteger(ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN, 512 * 1024);
    private final InputStream inputStream;
    private byte[] buffer = new byte[1024];
    private char[] array = new char[20];

    public DefaultSerializationInput(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    void readSize(int reserve) {
        try {
            if (reserve > buffer.length) {
                buffer = new byte[reserve + 1024];
            }
            int totalRead;
            if ((totalRead = inputStream.read(buffer, 0, reserve)) != reserve) {
                if (totalRead == -1) {
                    throw new EOFIOFailure("Missing data in buffer expected " + reserve + " but was " + totalRead);
                }
                loopRead(reserve, totalRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loopRead(int reserve, int totalRead) throws IOException {
        do {
            int read = inputStream.read(buffer, totalRead, reserve - totalRead);
            if (read == -1) {
                throw new EOFIOFailure("Missing data in buffer expected " + reserve + " but was " + totalRead);
            }
            totalRead += read;
        } while (totalRead != reserve);
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
        readSize(4);
        return (((buffer[0]) << 24) +
                ((buffer[1] & 255) << 16) +
                ((buffer[2] & 255) << 8) +
                ((buffer[3] & 255)));
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

    /*
    code from DataInputStream
     */
    public String readUtf8String() {
        int utflen = readNotNullInt();
        if (utflen == -1) {
            return null;
        }
        readSize(utflen);
        byte[] bytearr = buffer;
        if (array.length < utflen) {
            array = new char[utflen + 10];
        }
        char[] chararr = array;

        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++] = (char) c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0, 1, 2, 3, 4, 5, 6, 7 -> {
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++] = (char) c;
                }
                case 12, 13 -> {
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new RuntimeException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new RuntimeException(
                                "malformed input around byte " + count);
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) |
                                                       (char2 & 0x3F));
                }
                case 14 -> {
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new RuntimeException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new RuntimeException(
                                "malformed input around byte " + (count - 1));
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) |
                                                       ((char2 & 0x3F) << 6) |
                                                       ((char3 & 0x3F) << 0));
                }
                default ->
                    /* 10xx xxxx,  1111 xxxx */
                        throw new RuntimeException(
                                "malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);

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
        readSize(8);
        return readReservedLong();
    }

    private long readReservedLong() {
        return (((long)buffer[0] << 56) +
                ((long)(buffer[1] & 255) << 48) +
                ((long)(buffer[2] & 255) << 40) +
                ((long)(buffer[3] & 255) << 32) +
                ((long)(buffer[4] & 255) << 24) +
                ((buffer[5] & 255) << 16) +
                ((buffer[6] & 255) <<  8) +
                ((buffer[7] & 255)));
    }

    private int read() {
        try {
            int i = inputStream.read();
            if (i == -1) {
                throw new EOFIOFailure("eof");
            }
            return i;
        } catch (IOException e) {
            throw new EOFIOFailure(e);
        }
    }

    public byte readByte() {
        readSize(1);
        return buffer[0];
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
            return bytes;
        } catch (IOException e) {
            throw new UnexpectedApplicationState(e);
        }
    }

}
