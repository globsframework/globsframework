package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public abstract class AbstractFieldSetter<T extends FieldSetter> implements FieldSetter<T> {

    public T set(DoubleField field, Double value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(DoubleField field, double value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(DoubleArrayField field, double[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(IntegerField field, Integer value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(IntegerField field, int value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(IntegerArrayField field, int[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(StringField field, String value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(StringArrayField field, String[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(BooleanField field, Boolean value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(LongField field, Long value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(LongArrayField field, long[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(LongField field, long value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(BytesField field, byte[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(DateField field, LocalDate value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(GlobField field, Glob value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(GlobArrayField field, Glob[] values) throws ItemNotFound {
        return setValue(field, values);
    }

    public T set(GlobUnionField field, Glob value) throws ItemNotFound {
        return setValue(field, value);
    }

    public T set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound {
        return setValue(field, values);
    }
}
