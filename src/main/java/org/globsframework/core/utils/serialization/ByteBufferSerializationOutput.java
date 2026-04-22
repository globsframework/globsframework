package org.globsframework.core.utils.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public final class ByteBufferSerializationOutput implements SerializedOutput {
    static final VarHandle INT_VH =
            MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    static final VarHandle LONG_VH =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    private final ByteOutput outputStream;
    private final byte[] buffer;
    private final int limit;
    private int position = 0;
    public interface ByteOutput {
        void writeOutputBytes(byte[] b, int len);
    }

    public ByteBufferSerializationOutput(byte[] buffer) {
        this.buffer = buffer;
        limit = buffer.length - 8;
        outputStream = (b, len) -> {
            throw new RuntimeException("Not implemented");
        };
    }

    public int position() {
        return position;
    }

    public void reset() {
        position = 0;
    }

    public void reset(int position) {
        this.position = position;
    }

    public ByteBufferSerializationOutput(ByteOutput outputStream) {
        this(outputStream, 8192);
    }

    public ByteBufferSerializationOutput(ByteOutput outputStream, int bufferSize) {
        this.outputStream = outputStream;
        buffer = new byte[bufferSize];
        limit = bufferSize - 8;
    }

    public ByteBufferSerializationOutput(OutputStream outputStream) {
        this(outputStream, 8192);
    }

    public ByteBufferSerializationOutput(OutputStream outputStream, int bufferSize) {
        this.outputStream = new ByteOutput() {

            public void writeOutputBytes(byte[] b, int len) {
                try {
                    outputStream.write(b, 0, len);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        buffer = new byte[bufferSize];
        limit = bufferSize - 8;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void write(int[] values) {
        if (values == null) {
            write(-1);
        } else {
            if (reserve(values.length * 4 + 4)) {
                writeUncheckedInt(values.length);
                for (int value : values) {
                    writeUncheckedInt(value);
                }
            } else {
                writeUncheckedInt(values.length);
                for (int value : values) {
                    if (position + 4 >= buffer.length) {
                        flush();
                    }
                    writeUncheckedInt(value);
                }
            }
        }
    }

    public void write(long[] values) {
        if (values == null) {
            write(-1);
        } else {
            if (reserve(values.length * 8 + 4)) {
                writeUncheckedInt(values.length);
                for (long value : values) {
                    writeUncheckedLong(value);
                }
            } else {
                writeUncheckedInt(values.length);
                for (long value : values) {
                    if (position + 8 >= buffer.length) {
                        flush();
                    }
                    writeUncheckedLong(value);
                }
            }
        }
    }

    public void write(double[] values) {
        if (values == null) {
            write(-1);
        } else {
            if (reserve(values.length * 8 + 4)) {
                writeUncheckedInt(values.length);
                for (double value : values) {
                    writeUncheckedLong(Double.doubleToLongBits(value));
                }
            } else {
                writeUncheckedInt(values.length);
                for (double value : values) {
                    if (position + 8 >= buffer.length) {
                        flush();
                    }
                    writeUncheckedLong(Double.doubleToLongBits(value));
                }
            }
        }
    }

    public void write(String[] values) {
        if (values == null) {
            write(-1);
        } else {
            write(values.length);
            for (String value : values) {
                writeUtf8String(value);
            }
        }
    }

    public void write(BigDecimal[] values) {
        if (values == null) {
            write(-1);
        } else {
            write(values.length);
            for (BigDecimal value : values) {
                write(value);
            }
        }
    }

    public void write(BigDecimal value) {
        writeUtf8String(value.toPlainString());
    }

    // manual optimisation
    public void write(int value) {
        int p = position;
        if (p >= limit) {
            flush(4);
        }
        INT_VH.set(buffer, position, value);
        position += 4;
    }

    private void writeUncheckedInt(int value) {
//        int p = position;
//        final byte[] b = buffer;
//        b[p] = (byte) ((value >>> 24) & 0xFF);
//        b[p + 1] = (byte) ((value >>> 16) & 0xFF);
//        b[p + 2] = (byte) ((value >>> 8) & 0xFF);
//        b[p + 3] = (byte) ((value) & 0xFF);
        INT_VH.set(buffer, position, value);
        position += 4;
    }

    private boolean reserve(int len) {
        if (position + len >= buffer.length) {
            return flush(len);
        }
        return true;
    }

    private boolean flush(int len) {
        outputStream.writeOutputBytes(buffer, position);
        position = 0;
        return len <= buffer.length;
    }

    public void writeInteger(Integer value) {
        if (value == null) {
            writeByte(1);
        } else {
            reserve(5);
            writeUncheckByte((byte) 0);
            writeUncheckedInt(value);
        }
    }

    // manual optimisation
    public void write(long value) {
        if (position >= limit) {
            flush(8);
        }
        LONG_VH.set(buffer, position, value);
        position += 8;
    }

    private void writeUncheckedLong(long value) {
//        int p = position;
//        buffer[p] = ((byte) (value >>> 56));
//        buffer[p + 1] = ((byte) (value >>> 48));
//        buffer[p + 2] = ((byte) (value >>> 40));
//        buffer[p + 3] = ((byte) (value >>> 32));
//        buffer[p + 4] = ((byte) (value >>> 24));
//        buffer[p + 5] = ((byte) (value >>> 16));
//        buffer[p + 6] = ((byte) (value >>> 8));
//        buffer[p + 7] = ((byte) (value >>> 0));
        LONG_VH.set(buffer, position, value);
        position += 8;
    }

    public void writeLong(Long value) {
        if (value == null) {
            writeByte(1);
        } else {
            reserve(9);
            writeUncheckByte((byte) 0);
            writeUncheckedLong(value);
        }
    }

    public void write(double value) {
        write(Double.doubleToLongBits(value));
    }

    public void writeDouble(Double value) {
        if (value == null) {
            writeByte(1);
        } else {
            reserve(9);
            writeUncheckByte((byte) 0);
            writeUncheckedLong(Double.doubleToLongBits(value));
        }
    }

    /*
    code from DataOutputStream.
     */
    public void writeUtf8String(String str) {
        if (str == null) {
            write(-1);
        } else {
            final int strlen = str.length();
            int utflen = strlen; // optimized for ASCII

            boolean enoughSpace = 4 + strlen * 3 < buffer.length - position;
            if (!enoughSpace) {
                enoughSpace = 4 + strlen * 3 < buffer.length;
                if (enoughSpace) {
                    flush();
                }
            }
            if (enoughSpace) {
                int previousPos  = position;
                position += 4; // place for int
                int newPos = write(str, 0, strlen, buffer, position);
                position = previousPos;
                writeUncheckedInt(newPos - previousPos - 4);
                position = newPos;
            } else {
                for (int i = 0; i < strlen; i++) {
                    int c = str.charAt(i);
                    if (c >= 0x80 || c == 0)
                        utflen += (c >= 0x800) ? 2 : 1;
                }

                if (reserve(utflen + 4)) {
                    writeUncheckedInt(utflen);
                    write(str, 0, strlen, buffer, position);
                    position += utflen;
                } else {
                    writeUncheckedInt(utflen);
                    int startAt = 0;
                    while (true) {
                        final int toRead = Math.min(strlen - startAt, (buffer.length - position) / 3 - 1);
                        position = write(str, startAt, toRead, buffer, position);
                        startAt += toRead;
                        if (startAt == strlen) {
                            return;
                        }
                        flush();
                    }
                }
            }
        }
    }

    private int write(String str, int startAt, int strlen, byte[] buffer, int position) {
        int i;
        strlen += startAt;
        for (i = startAt; i < strlen; i++) {
            int c = str.charAt(i);
            if (c >= 0x80 || c == 0) break;
            buffer[position++] = (byte) c;
        }

        for (; i < strlen; i++) {
            int c = str.charAt(i);
            if (c < 0x80 && c != 0) {
                buffer[position++] = (byte) c;
            } else if (c >= 0x800) {
                buffer[position++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buffer[position++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buffer[position++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                buffer[position++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                buffer[position++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        return position;
    }

    public void write(boolean value) {
        writeByte(value ? 1 : 0);
    }

    public void writeBoolean(Boolean value) {
        if (value == null) {
            writeByte(2);
        } else {
            writeByte(value ? 1 : 0);
        }
    }

    public void write(boolean[] value) {
        if (value == null) {
            write(-1);
            return;
        }
        if (reserve(value.length + 4)) {
            writeUncheckedInt(value.length);
            int p = position;
            for (int i = 0; i < value.length; i++) {
                buffer[p++] = (byte) (value[i] ? 1 : 0);
            }
            position = p;
        } else {
            writeChecked(value);
        }
    }

    private void writeChecked(boolean[] value) {
        writeUncheckedInt(value.length);
        int p = position;
        for (int i = 0; i < value.length; i++) {
            if (p >= buffer.length) {
                flush(1);
                p = 0;
            }
            buffer[p++] = (byte) (value[i] ? 1 : 0);
        }
        position = p;
    }

    public void writeByte(int value) {
        if (position >= limit) {
            flush(1);
        }
        buffer[position++] = (byte) value;
    }

    private void writeUncheckByte(byte value) {
        buffer[position++] = value;
    }

    public void writeByte(byte value) {
        if (position >= limit) {
            flush(1);
        }
        buffer[position++] = (byte) value;
    }

    public void writeBytes(byte[] value) {
        if (value == null) {
            write(-1);
            return;
        }

        if (reserve(value.length + 4)) {
            writeUncheckedInt(value.length);
            System.arraycopy(value, 0, buffer, position, value.length);
            position += value.length;
        } else {
            outputStream.writeOutputBytes(value, value.length);
        }
    }

    public void writeDate(LocalDate date) {
        if (date == null) {
            write(Integer.MIN_VALUE);
        } else {
            if (reserve(4 * 3)) {
                writeUncheckedInt(date.getYear());
                writeUncheckedInt(date.getMonthValue());
                writeUncheckedInt(date.getDayOfMonth());
            } else {
                write(date.getYear());
                write(date.getMonthValue());
                write(date.getDayOfMonth());
            }
        }
    }

    public void writeDateTime(ZonedDateTime date) {
        if (date == null) {
            write(Integer.MIN_VALUE);
        } else {
            if (reserve(4 * 7)) {
                writeUncheckedInt(date.getYear());
                writeUncheckedInt(date.getMonthValue());
                writeUncheckedInt(date.getDayOfMonth());
                writeUncheckedInt(date.getHour());
                writeUncheckedInt(date.getMinute());
                writeUncheckedInt(date.getSecond());
                writeUncheckedInt(date.getNano());
            } else {
                write(date.getYear());
                write(date.getMonthValue());
                write(date.getDayOfMonth());
                write(date.getHour());
                write(date.getMinute());
                write(date.getSecond());
                write(date.getNano());
            }
            writeUtf8String(date.getZone().getId());
        }
    }

    public void flush() {
        if (position > 0) {
            outputStream.writeOutputBytes(buffer, position);
            position = 0;
        }
    }
}
