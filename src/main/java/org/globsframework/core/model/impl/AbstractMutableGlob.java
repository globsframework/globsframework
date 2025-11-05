package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.model.*;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public interface AbstractMutableGlob extends AbstractGlob, MutableGlob {

    default MutableGlob set(IntegerField field, Integer value) {
        return setObject(field, value);
    }

    default MutableGlob set(IntegerField field, int value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(IntegerArrayField field, int[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(DoubleField field, Double value) {
        return setObject(field, value);
    }

    default MutableGlob set(DoubleField field, double value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(DoubleArrayField field, double[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(LongField field, Long value) {
        return setObject(field, value);
    }

    default MutableGlob set(LongField field, long value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(LongArrayField field, long[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(StringField field, String value) {
        return setObject(field, value);
    }

    default MutableGlob set(StringArrayField field, String[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(BooleanField field, Boolean value) {
        return setObject(field, value);
    }

    default MutableGlob set(BytesField field, byte[] value) {
        return setObject(field, value);
    }

    default MutableGlob setValue(Field field, Object value) {
        return setObject(field, value);
    }

    default MutableGlob set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(DateField field, LocalDate value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(GlobField field, Glob value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(GlobArrayField field, Glob[] values) throws ItemNotFound {
        return setObject(field, values);
    }

    default MutableGlob set(GlobUnionField field, Glob value) throws ItemNotFound {
        return setObject(field, value);
    }

    default MutableGlob set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound {
        return setObject(field, values);
    }

    default MutableGlob setValues(FieldValues values) {
        values.safeApply(this::setObject);
        return this;
    }

    default Key getTargetKey(Link link) {
        if (!link.getSourceType().equals(getType())) {
            throw new InvalidParameter("Link '" + link + " cannot be used with " + this);
        }

        KeyBuilder keyBuilder = KeyBuilder.init(link.getTargetType());
        link.apply((sourceField, targetField) -> {
            Object value = getValue(sourceField);
            keyBuilder.setObject(targetField, value);

        });
        return keyBuilder.get();
    }

    default FieldValues getValues() {
        FieldValuesBuilder builder = FieldValuesBuilder.init();
        for (Field field : getType().getFields()) {
            if (!field.isKeyField()) {
                builder.setValue(field, doGet(field));
            }
        }
        return builder.get();
    }

    default FieldValue[] toArray() {
        List<FieldValue> fieldValueList = new ArrayList<>();
        for (Field field : getType().getFields()) {
            if (isSet(field)) {
                fieldValueList.add(new FieldValue(field, doGet(field)));
            }
        }
        return fieldValueList.toArray(FieldValue[]::new);
    }

    default Object doCheckedGet(Field field) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            FieldCheck.check(field, getType());
            checkReserved();
        }
        return doGet(field);
    }

    void checkReserved();

    default MutableGlob setObject(Field field, Object value) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            if (isHashComputed() && field.isKeyField()) {
                throw new RuntimeException(field.getFullName() + " is a key value and the hashCode is already computed.");
            }
            FieldCheck.check(field, getType(), value);
            checkReserved();
        }
        return doSet(field, value);
    }

    boolean isHashComputed();

    MutableGlob doSet(Field field, Object value);

    default Key getKey() {
        return this;
    }

}
