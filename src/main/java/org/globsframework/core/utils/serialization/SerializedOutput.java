package org.globsframework.core.utils.serialization;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface SerializedOutput {
    void write(int value);

    void writeInteger(Integer value);

    void write(double value);

    void writeDouble(Double value);

    void write(boolean value);

    void writeBoolean(Boolean value);

    void write(long value);

    void writeLong(Long value);

    void writeByte(int value);

    void writeByte(byte value);

    void writeBytes(byte[] value);

    void writeUtf8String(String value);

    void write(BigDecimal value);

    void write(int[] values);

    void write(long[] values);

    void write(double[] values);

    void write(String[] values);

    void write(boolean[] values);

    void write(BigDecimal[] values);

    default void writeString(String s) {
        writeUtf8String(s);
    }

    void writeDate(LocalDate date);

    void writeDateTime(ZonedDateTime date);
}
