package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.FieldSetter;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableFieldValues;
import org.globsframework.core.model.impl.AbstractFieldValues;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public abstract class AbstractMutableFieldValues implements AbstractFieldValues, MutableFieldValues {

    public abstract MutableFieldValues setValue(Field field, Object value) throws InvalidParameter;

    public MutableFieldValues set(IntegerField field, Integer value) {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(DoubleField field, Double value) {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(StringField field, String value) {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(BooleanField field, Boolean value) {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(LongField field, Long value) throws ItemNotFound {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(BytesField field, byte[] value) {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(DoubleField field, double value) throws ItemNotFound {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(IntegerField field, int value) throws ItemNotFound {
        setValue(field, value);
        return this;
    }

    public MutableFieldValues set(LongField field, long value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(DoubleArrayField field, double[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(IntegerArrayField field, int[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(StringArrayField field, String[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(LongArrayField field, long[] value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(DateField field, LocalDate value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        return setValue(field, value);
    }

    public FieldSetter set(GlobField field, Glob value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(GlobArrayField field, Glob[] values) throws ItemNotFound {
        return setValue(field, values);
    }

    public MutableFieldValues set(GlobUnionField field, Glob value) throws ItemNotFound {
        return setValue(field, value);
    }

    public MutableFieldValues set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound {
        return setValue(field, values);
    }

    public void setValues(FieldValues values) {
        values.safeApply(new Functor() {
            public void process(Field field, Object value) throws IOException {
                setValue(field, value);
            }
        });
    }

    public FieldSetter set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        return setValue(field, value);
    }
}
