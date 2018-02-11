package org.globsframework.model.impl;

import org.globsframework.metamodel.Field;
import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.fields.*;
import org.globsframework.model.FieldValues;
import org.globsframework.model.MutableGlob;
import org.globsframework.model.format.GlobPrinter;
import org.globsframework.utils.exceptions.ItemNotFound;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public abstract class AbstractMutableGlob extends AbstractGlob implements MutableGlob {
    protected AbstractMutableGlob(GlobType type) {
        super(type);
    }

    public AbstractMutableGlob(GlobType type, Object[] values) {
        super(type, values);
    }

    public MutableGlob set(IntegerField field, Integer value) {
        setObject(field, value);
        return this;
    }

    public MutableGlob set(DoubleField field, Double value) {
        setObject(field, value);
        return this;
    }

    public MutableGlob set(StringField field, String value) {
        setObject(field, value);
        return this;
    }

    public MutableGlob set(LongField field, Long value) {
        setObject(field, value);
        return this;
    }

    public MutableGlob set(LongArrayField field, long[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(BooleanField field, Boolean value) {
        return setObject(field, value);
    }

    public MutableGlob set(BlobField field, byte[] value) {
        return setObject(field, value);
    }

    public MutableGlob set(DoubleField field, double value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(IntegerField field, int value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(LongField field, long value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob setValue(Field field, Object value) {
        return setObject(field, value);
    }

    public MutableGlob set(DoubleArrayField field, double[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(IntegerArrayField field, int[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(StringArrayField field, String[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(DateField field, LocalDate value) throws ItemNotFound {
        return setObject(field, value);
    }

    public MutableGlob set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        return setObject(field, value);
    }


    public MutableGlob setValues(FieldValues values) {
        values.safeApply(new FieldValues.Functor() {
            public void process(Field field, Object value) throws IOException {
                setObject(field, value);
            }
        });
        return this;
    }

    public MutableGlob setObject(Field field, Object value) {
        if (field.isKeyField() && key != null) {
            throw new RuntimeException("Bug mutable glob key field can not be change after access to the key " + field.getFullName() + ", " + value + "  : " + GlobPrinter.toString(this));
        }
        final int index = field.getIndex();
        values[index] = value;
        return this;
    }
}
