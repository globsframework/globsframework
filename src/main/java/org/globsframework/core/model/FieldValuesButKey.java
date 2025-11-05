package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

public class FieldValuesButKey implements FieldValues {
    private final FieldValues fieldValues;

    public FieldValuesButKey(FieldValues fieldValues) {
        this.fieldValues = fieldValues;
    }

    public boolean contains(Field field) {
        return !field.isKeyField() && fieldValues.contains(field);
    }

    public int size() {
        return fieldValues.safeApply(new Functor() {
            int keyCount = 0;

            public void process(Field field, Object value) {
                if (!field.isKeyField()) {
                    keyCount++;
                }
            }
        }).keyCount;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        fieldValues.apply(functor.withoutKeyField());
        return functor;
    }

    public <T extends Functor> T safeApply(T functor) {
        fieldValues.safeApply(functor.withoutKeyField());
        return functor;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        fieldValues.accept(functor.withoutKey());
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        fieldValues.accept(functor.withoutKey(), ctx);
        return functor;
    }

    public <T extends FieldValueVisitor> T safeAccept(T functor) {
        fieldValues.safeAccept(functor.withoutKey());
        return functor;
    }

    public FieldValue[] toArray() {
        FieldValue[] fieldValues = this.fieldValues.toArray();
        for (FieldValue fieldValue : fieldValues) {
            if (fieldValue.getField().isKeyField()) {
                return Arrays.stream(fieldValues).filter(Objects::nonNull).filter(v -> !v.getField().isKeyField()).toArray(FieldValue[]::new);
            }
        }
        return fieldValues;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.isSet(field);
    }

    public boolean isNull(Field field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.isNull(field);
    }

    private void checkNotAKey(Field field) {
        if (field.isKeyField()) {
            throw new RuntimeException("trying to retreive a value for a key field is not exptected in a FieldValueWithoutKey");
        }
    }

    public Object getValue(Field field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.getValue(field);
    }

    public Double get(DoubleField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public double get(DoubleField field, double valueIfNull) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field, valueIfNull);
    }

    public double[] get(DoubleArrayField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public Integer get(IntegerField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public int get(IntegerField field, int valueIfNull) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field, valueIfNull);
    }

    public int[] get(IntegerArrayField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public String get(StringField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public String[] get(StringArrayField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public Boolean get(BooleanField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public boolean get(BooleanField field, boolean defaultIfNull) {
        checkNotAKey(field);
        return fieldValues.get(field, defaultIfNull);
    }

    public boolean[] get(BooleanArrayField field) {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public boolean isTrue(BooleanField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.isTrue(field);
    }

    public Long get(LongField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public long get(LongField field, long valueIfNull) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field, valueIfNull);
    }

    public long[] get(LongArrayField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public BigDecimal get(BigDecimalField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public LocalDate get(DateField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public ZonedDateTime get(DateTimeField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public byte[] get(BytesField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public Glob get(GlobField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public Glob[] get(GlobArrayField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public Glob get(GlobUnionField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }

    public Glob[] get(GlobArrayUnionField field) throws ItemNotFound {
        checkNotAKey(field);
        return fieldValues.get(field);
    }
}
