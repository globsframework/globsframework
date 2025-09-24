package org.globsframework.core.utils.serialization;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class NByteBufferSerializationInput implements SerializedInput {
    private final NextBuffer nextBuffer;
    private ByteBuffer data;
    private byte[] buffer = new byte[1024];

    public NByteBufferSerializationInput(ByteBuffer data, NextBuffer nextBuffer) {
        this.data = data;
        this.nextBuffer = nextBuffer;
    }

    public interface NextBuffer {
        ByteBuffer refill(ByteBuffer data);
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
        if (data.remaining() >= 4){
            return data.getInt();
        }
        blockingRead(4);
        return (((buffer[0]) << 24)
                + ((buffer[1] & 0xff) << 16)
                + ((buffer[2] & 0xff) << 8)
                + ((buffer[3] & 0xff) << 0x0));
    }

    private void blockingRead(int wantedByte) {
        if (wantedByte == 0) {
            return;
        }
        if (buffer == null || buffer.length < wantedByte) {
            buffer = new byte[wantedByte];
        }
        int offset = 0;
        while (true) {
            final int toRead = Math.min(wantedByte - offset, data.remaining());
            data.get(buffer, offset, toRead);
            offset += toRead;
            if (offset == wantedByte) {
                return;
            }
            nextBuffer();
        }
    }

    private void nextBuffer() {
        data = nextBuffer.refill(data);
    }

    private boolean isNull() {
        return read() != 0;
    }

    public Double readDouble() {
        if (isNull()) {
            return null;
        }
        return readNotNullDouble();
    }

    public double readNotNullDouble() {
        if (data.remaining() >= 8){
            return data.getDouble();
        }
        return Double.longBitsToDouble(readNotNullLong());
    }

    public String readUtf8String() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return "";
        }
        if (data.remaining() >= length) {
            if (buffer == null || buffer.length < length) {
                buffer = new byte[length];
            }
            data.get(buffer, 0, length);
        }
        else {
            blockingRead(length);
        }
        return new String(buffer, 0, length, StandardCharsets.UTF_8);
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
        if (data.remaining() >= 8){
            return data.getLong();
        }
        blockingRead(8);
        return (((long) (buffer[0]) << 56) +
                ((long) (buffer[1] & 0xff) << 48) +
                ((long) (buffer[2] & 0xff) << 40) +
                ((long) (buffer[3] & 0xff) << 32) +
                ((long) (buffer[4] & 0xff) << 24) +
                ((long) (buffer[5] & 0xff) << 16) +
                ((long) (buffer[6] & 0xff) << 8) +
                (       (buffer[7] & 0xff)));
    }

    private int read() {
        if (data.remaining() >= 1){
            return data.get() & 0xff;
        }
        blockingRead(1);
        return buffer[0] & 0xff;
    }

    public byte readByte() {
        return (byte) read();
    }

    public byte[] readBytes() {
        int length = readNotNullInt();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new byte[0];
        }
        if (data.remaining() >= length){
            byte[] tmp = new byte[length];
            data.get(tmp, 0, length);
            return tmp;
        }
        else {
            blockingRead(length);
            return Arrays.copyOfRange(buffer, 0, length);
        }
    }
}
