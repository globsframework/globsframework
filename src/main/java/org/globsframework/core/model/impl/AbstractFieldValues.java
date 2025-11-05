package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.Glob;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface AbstractFieldValues extends FieldValues {

    default Double get(DoubleField field) {
        return (Double) doCheckedGet(field);
    }

    default double get(DoubleField field, double valueIfNull) throws ItemNotFound {
        Object o = doCheckedGet(field);
        if (o == null) {
            return valueIfNull;
        }
        return (Double) o;
    }

    default Integer get(IntegerField field) {
        return (Integer) doCheckedGet(field);
    }

    default int get(IntegerField field, int valueIfNull) throws ItemNotFound {
        Integer value = (Integer) doCheckedGet(field);
        if (value == null) {
            return valueIfNull;
        }
        return value;
    }

    default String get(StringField field) {
        return (String) doCheckedGet(field);
    }

    default Boolean get(BooleanField field) {
        return (Boolean) doCheckedGet(field);
    }

    default boolean isTrue(BooleanField field) {
        return Boolean.TRUE.equals(doCheckedGet(field));
    }

    default boolean isNull(Field field) throws ItemNotFound {
        return doCheckedGet(field) == null;
    }

    default Object getValue(Field field) {
        return doCheckedGet(field);
    }

    default byte[] get(BytesField field) {
        return (byte[]) doCheckedGet(field);
    }

    default boolean get(BooleanField field, boolean defaultIfNull) {
        Object value = doCheckedGet(field);
        return value == null ? Boolean.valueOf(defaultIfNull) : (boolean) value;
    }

    default Long get(LongField field) {
        return (Long) doCheckedGet(field);
    }

    default long get(LongField field, long valueIfNull) throws ItemNotFound {
        Object value = doCheckedGet(field);
        return value == null ? valueIfNull : (long) value;
    }

    default double[] get(DoubleArrayField field) throws ItemNotFound {
        return (double[]) doCheckedGet(field);
    }

    default int[] get(IntegerArrayField field) throws ItemNotFound {
        return (int[]) doCheckedGet(field);
    }

    default String[] get(StringArrayField field) throws ItemNotFound {
        return (String[]) doCheckedGet(field);
    }

    default boolean[] get(BooleanArrayField field) {
        return (boolean[]) doCheckedGet(field);
    }

    default long[] get(LongArrayField field) throws ItemNotFound {
        return (long[]) doCheckedGet(field);
    }

    default LocalDate get(DateField field) throws ItemNotFound {
        return (LocalDate) doCheckedGet(field);
    }

    default ZonedDateTime get(DateTimeField field) throws ItemNotFound {
        return (ZonedDateTime) doCheckedGet(field);
    }

    default BigDecimal get(BigDecimalField field) throws ItemNotFound {
        return (BigDecimal) doCheckedGet(field);
    }

    default BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound {
        return (BigDecimal[]) doCheckedGet(field);
    }

    default Glob get(GlobField field) throws ItemNotFound {
        return (Glob) doCheckedGet(field);
    }

    default Glob[] get(GlobArrayField field) throws ItemNotFound {
        return (Glob[]) doCheckedGet(field);
    }

    default Glob get(GlobUnionField field) throws ItemNotFound {
        return (Glob) doCheckedGet(field);
    }

    default Glob[] get(GlobArrayUnionField field) throws ItemNotFound {
        return (Glob[]) doCheckedGet(field);
    }

     Object doCheckedGet(Field field);

}
