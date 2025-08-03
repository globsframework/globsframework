package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.exceptions.InvalidData;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

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

    public interface ByteOutput {
        void writeOutputByte(int b) throws IOException;
        void writeOutputBytes(byte[] b) throws IOException;
    }

    public DefaultSerializationOutput(ByteOutput outputStream) {
        this.outputStream = outputStream;
    }

    public DefaultSerializationOutput(OutputStream outputStream) {
        this.outputStream = new ByteOutput() {
            public void writeOutputByte(int b) throws IOException {
                outputStream.write(b);
            }

            public void writeOutputBytes(byte[] b) throws IOException {
                outputStream.write(b);
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
        try {
            outputStream.writeOutputByte((value >>> 24) & 0xFF);
            outputStream.writeOutputByte((value >>> 16) & 0xFF);
            outputStream.writeOutputByte((value >>> 8) & 0xFF);
            outputStream.writeOutputByte((value >>> 0) & 0xFF);
//            value >>= 8;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            outputStream.writeOutputByte((byte) (value >>> 56));
            outputStream.writeOutputByte((byte) (value >>> 48));
            outputStream.writeOutputByte((byte) (value >>> 40));
            outputStream.writeOutputByte((byte) (value >>> 32));
            outputStream.writeOutputByte((byte) (value >>> 24));
            outputStream.writeOutputByte((byte) (value >>> 16));
            outputStream.writeOutputByte((byte) (value >>> 8));
            outputStream.writeOutputByte((byte) (value >>> 0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void writeDate(Date date) {
        if (date == null) {
            write(-1L);
        } else {
            write(date.getTime());
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
        try {
            if (value == null) {
                write(-1);
                return;
            }
            write(value.length);
            for (boolean b : value) {
                outputStream.writeOutputByte(b ? 1 : 0);
            }
        } catch (IOException e) {
            throw new UnexpectedApplicationState(e);
        }
    }

    public void writeByte(int value) {
        try {
            outputStream.writeOutputByte(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeByte(byte value) {
        try {
            outputStream.writeOutputByte(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBytes(byte[] value) {
        try {
            if (value == null) {
                int value1 = -1;
                write(value1);
                return;
            }
            if (MAX_SIZE_FOR_BYTES != -1 && value.length > MAX_SIZE_FOR_BYTES) {
                throw new InvalidData("More than " + MAX_SIZE_FOR_BYTES + " bytes to write  (" + value.length + ") see " + ORG_GLOBSFRAMWORK_SERIALIZATION_MAX_LEN);
            }
            write(value.length);
            outputStream.writeOutputBytes(value);
        } catch (IOException e) {
            throw new UnexpectedApplicationState(e);
        }
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
