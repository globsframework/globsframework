package org.globsframework.core.utils.serialization;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public final class NByteBufferSerializationOutput implements SerializedOutput {
    private final ByteOutput outputStream;
    private ByteBuffer buffer;

    public interface ByteOutput {
        ByteBuffer writeOutputBytes(ByteBuffer byteBuffer);
    }

    public NByteBufferSerializationOutput(int size) {
        this((b) -> {
            throw new RuntimeException("Not implemented");
        }, ByteBuffer.allocateDirect(size));
    }

    public NByteBufferSerializationOutput(ByteBuffer byteBuffer) {
        this((b) -> {
            throw new RuntimeException("Not implemented");
        }, byteBuffer);
    }

    public NByteBufferSerializationOutput(ByteOutput outputStream, ByteBuffer byteBuffer) {
        this.outputStream = outputStream;
        this.buffer = byteBuffer;
    }

    public NByteBufferSerializationOutput(ByteOutput outputStream, int bufferSize) {
        this(outputStream, ByteBuffer.allocateDirect(bufferSize));
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
                    if (buffer.position() + 4 >= buffer.limit()) {
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
                    if (buffer.position() + 8 >= buffer.limit()) {
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
                    if (buffer.position() + 8 >= buffer.limit()) {
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

    public void write(int value) {
        if (buffer.remaining() < 4) {
            flush(4);
        }
        buffer.putInt(value);
    }

    private void writeUncheckedInt(int value) {
        buffer.putInt(value);
    }

    private boolean reserve(int len) {
        if (buffer.position() + len >= buffer.limit()) {
            return flush(len);
        }
        return true;
    }

    private boolean flush(int len) {
        if (buffer.position() > 0) {
            buffer = outputStream.writeOutputBytes(buffer);
        }
        return len <= buffer.limit();
    }

    public void flush() {
        if (buffer.position() > 0) {
            buffer = outputStream.writeOutputBytes(buffer);
        }
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

    public void write(long value) {
        if (buffer.remaining() < 8) {
            flush(8);
        }
        buffer.putLong(value);
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
        buffer.putLong(value);
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

            boolean enoughSpace = strlen < 1000000 && 4 + strlen * 3 < buffer.limit() - buffer.position();
            if (!enoughSpace) {
                enoughSpace = strlen < 1000000 && 4 + strlen * 3 < buffer.limit();
                if (enoughSpace) {
                    flush();
                }
            }
            if (enoughSpace) {
                int previousPos = buffer.position();
                buffer.position(previousPos + 4); // place for int
                write(str, 0, strlen, buffer);
                int newPos = buffer.position();
                buffer.position(previousPos);
                writeUncheckedInt(newPos - previousPos - 4);
                buffer.position(newPos);
            } else {
                for (int i = 0; i < strlen; i++) {
                    int c = str.charAt(i);
                    if (c >= 0x80 || c == 0)
                        utflen += (c >= 0x800) ? 2 : 1;
                }

                if (reserve(utflen + 4)) {
                    writeUncheckedInt(utflen);
                    write(str, 0, strlen, buffer);
                } else {
                    writeUncheckedInt(utflen);
                    int startAt = 0;
                    while (true) {
                        final int toRead = Math.min(strlen - startAt, (buffer.remaining()) / 3 - 1);
                        write(str, startAt, toRead, buffer);
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

    private void write(String str, int startAt, int strlen, ByteBuffer buffer) {
        int i;
        strlen += startAt;
        for (i = startAt; i < strlen; i++) {
            int c = str.charAt(i);
            if (c >= 0x80 || c == 0) break;
            buffer.put((byte) c);
        }

        for (; i < strlen; i++) {
            int c = str.charAt(i);
            if (c < 0x80 && c != 0) {
                buffer.put((byte) c);
            } else if (c >= 0x800) {
                buffer.put((byte) (0xE0 | ((c >> 12) & 0x0F)));
                buffer.put((byte) (0x80 | ((c >> 6) & 0x3F)));
                buffer.put((byte) (0x80 | ((c >> 0) & 0x3F)));
            } else {
                buffer.put((byte) (0xC0 | ((c >> 6) & 0x1F)));
                buffer.put((byte) (0x80 | ((c >> 0) & 0x3F)));
            }
        }
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
            for (int i = 0; i < value.length; i++) {
                buffer.put((byte) (value[i] ? 1 : 0));
            }
        } else {
            writeChecked(value);
        }
    }

    private void writeChecked(boolean[] value) {
        writeUncheckedInt(value.length);
        for (int i = 0; i < value.length; i++) {
            if (!buffer.hasRemaining()) {
                flush(1);
            }
            buffer.put((byte) (value[i] ? 1 : 0));
        }
    }

    public void writeByte(int value) {
        if (!buffer.hasRemaining()) {
            flush(1);
        }
        buffer.put((byte) value);
    }

    private void writeUncheckByte(byte value) {
        buffer.put(value);
    }

    public void writeByte(byte value) {
        if (!buffer.hasRemaining()) {
            flush(1);
        }
        buffer.put((byte) value);
    }

    public void writeBytes(byte[] value) {
        if (value == null) {
            write(-1);
            return;
        }

        if (reserve(value.length + 4)) {
            writeUncheckedInt(value.length);
            buffer.put(value);
        } else {
            write(value.length);
            int pos = 0;
            while (pos != value.length) {
                int toWrite = Math.min(value.length - pos, buffer.remaining());
                buffer.put(value, pos, pos + toWrite);
                pos += toWrite;
                if (pos != value.length) {
                    flush();
                }
            }
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
}
