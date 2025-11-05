package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface FieldSetter<T extends FieldSetter> {
    T set(DoubleField field, Double value) throws ItemNotFound;

    T set(DoubleField field, double value) throws ItemNotFound;

    T set(DoubleArrayField field, double[] value) throws ItemNotFound;

    T set(IntegerField field, Integer value) throws ItemNotFound;

    T set(IntegerField field, int value) throws ItemNotFound;

    T set(IntegerArrayField field, int[] value) throws ItemNotFound;

    T set(StringField field, String value) throws ItemNotFound;

    T set(StringArrayField field, String[] value) throws ItemNotFound;

    T set(BooleanField field, Boolean value) throws ItemNotFound;

    T set(BooleanArrayField field, boolean[] value) throws ItemNotFound;

    T set(LongField field, Long value) throws ItemNotFound;

    T set(LongArrayField field, long[] value) throws ItemNotFound;

    T set(LongField field, long value) throws ItemNotFound;

    T set(BigDecimalField field, BigDecimal value) throws ItemNotFound;

    T set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound;

    T set(BytesField field, byte[] value) throws ItemNotFound;

    T set(DateField field, LocalDate value) throws ItemNotFound;

    T set(DateTimeField field, ZonedDateTime value) throws ItemNotFound;

    T set(GlobField field, Glob value) throws ItemNotFound;

    T set(GlobArrayField field, Glob[] values) throws ItemNotFound;

    T set(GlobUnionField field, Glob value) throws ItemNotFound;

    T set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound;

    T setValue(Field field, Object value) throws ItemNotFound;
}
