package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.exceptions.InvalidData;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.globsframework.core.utils.serialization.DefaultSerializationInput.MAX_SIZE_FOR_BYTES;
import static org.globsframework.core.utils.serialization.DefaultSerializationInput.ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN;

public class DefaultSerializationOutput implements SerializedOutput {
    private final ByteOutput outputStream;
    private final byte[] buffer = new byte[8];

    public interface ByteOutput {
        void writeOutputByte(int b);

        void writeOutputBytes(byte[] b, int len);
    }

    public DefaultSerializationOutput(ByteOutput outputStream) {
        this.outputStream = outputStream;
    }

    public DefaultSerializationOutput(OutputStream outputStream) {
        this.outputStream = new ByteOutput() {
            public void writeOutputByte(int b)  {
                try {
                    outputStream.write(b);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public void writeOutputBytes(byte[] b, int len)  {
                try {
                    outputStream.write(b, 0, len);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void write(int[] values) {
        if (values == null) {
            write(-1);
        } else {
            write(values.length);
            for (int value : values) {
                write(value);
            }
        }
    }

    public void write(long[] values) {
        if (values == null) {
            write(-1);
        } else {
            write(values.length);
            for (long value : values) {
                write(value);
            }
        }
    }

    public void write(double[] values) {
        if (values == null) {
            write(-1);
        } else {
            write(values.length);
            for (double value : values) {
                write(value);
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
        buffer[0] = (byte) ((value >>> 24) & 0xFF);
        buffer[1] = (byte) ((value >>> 16) & 0xFF);
        buffer[2] = (byte) ((value >>> 8) & 0xFF);
        buffer[3] = (byte) ((value >>> 0) & 0xFF);
        outputStream.writeOutputBytes(buffer, 4);
    }

    public void writeInteger(Integer value) {
        if (value == null) {
            writeByte(1);
        } else {
            writeByte(0);
            write(value);
        }
    }

    public void write(long value) {
        buffer[0] = (byte) ((value >>> 56) & 0xFF);
        buffer[1] = (byte) ((value >>> 48) & 0xFF);
        buffer[2] = (byte) ((value >>> 40) & 0xFF);
        buffer[3] = (byte) ((value >>> 32) & 0xFF);
        buffer[4] = (byte) ((value >>> 24) & 0xFF);
        buffer[5] = (byte) ((value >>> 16) & 0xFF);
        buffer[6] = (byte) ((value >>> 8) & 0xFF);
        buffer[7] = (byte) ((value >>> 0) & 0xFF);
        outputStream.writeOutputBytes(buffer, 8);
    }

    public void writeLong(Long value) {
        if (value == null) {
            writeByte(1);
        } else {
            writeByte(0);
            write(value);
        }
    }

    public void write(double value) {
        write(Double.doubleToLongBits(value));
    }

    public void writeDouble(Double value) {
        if (value == null) {
            writeByte(1);
        } else {
            writeByte(0);
            write(Double.doubleToLongBits(value));
        }
    }

    public void writeUtf8String(String value) {
        if (value == null) {
            writeBytes(null);
        } else {
            writeBytes(value.getBytes(StandardCharsets.UTF_8));
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
        write(value.length);
        for (boolean b : value) {
            outputStream.writeOutputByte(b ? 1 : 0);
        }
    }

    public void writeByte(int value) {
        outputStream.writeOutputByte(value);
    }

    public void writeByte(byte value) {
        outputStream.writeOutputByte(value);
    }

    public void writeBytes(byte[] value) {
        if (value == null) {
            int value1 = -1;
            write(value1);
            return;
        }
        if (MAX_SIZE_FOR_BYTES != -1 && value.length > MAX_SIZE_FOR_BYTES) {
            throw new InvalidData("More than " + MAX_SIZE_FOR_BYTES + " bytes to write  (" + value.length + ") see " + ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN);
        }
        write(value.length);
        outputStream.writeOutputBytes(value, value.length);
    }

    public void writeDate(LocalDate date) {
        if (date == null) {
            write(Integer.MIN_VALUE);
        } else {
            write(date.getYear());
            write(date.getMonthValue());
            write(date.getDayOfMonth());
        }
    }

    public void writeDateTime(ZonedDateTime date) {
        if (date == null) {
            write(Integer.MIN_VALUE);
        } else {
            write(date.getYear());
            write(date.getMonthValue());
            write(date.getDayOfMonth());
            write(date.getHour());
            write(date.getMinute());
            write(date.getSecond());
            write(date.getNano());
            writeUtf8String(date.getZone().getId());
        }
    }
}
