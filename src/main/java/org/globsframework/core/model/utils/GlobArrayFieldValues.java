package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.MutableFieldValues;
import org.globsframework.core.utils.Unset;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Arrays;

public class GlobArrayFieldValues extends AbstractMutableFieldValues {
    private GlobType type;
    private Object[] values;

    public GlobArrayFieldValues(GlobType type, Object[] values) throws InvalidParameter {
        this.type = type;
        this.values = values;
        if (values.length != type.getFieldCount()) {
            throw new InvalidParameter("Values should have " + type.getFieldCount() + " elements instead of " +
                    values.length + " for type " + type.getName() +
                    " - array content: " + Arrays.toString(values));
        }
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return values[field.getIndex()] != Unset.VALUE;
    }

    public Object doCheckedGet(Field field) {
        Object value = values[field.getIndex()];
        return value == Unset.VALUE ? null : value;
    }

    public boolean contains(Field field) {
        return type.equals(field.getGlobType());
    }

    public int size() {
        return values.length;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (Field field : type.getFields()) {
            Object value = values[field.getIndex()];
            if (value != Unset.VALUE) {
                field.acceptValue(functor, value);
            }
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        for (Field field : type.getFields()) {
            Object value = values[field.getIndex()];
            if (value != Unset.VALUE) {
                field.acceptValue(functor, value, ctx);
            }
        }
        return functor;
    }

    public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
        for (Field field : type.getFields()) {
            functor.process(field, values[field.getIndex()]);
        }
        return functor;
    }

    public FieldValue[] toArray() {
        FieldValue[] result = new FieldValue[values.length];
        int index = 0;
        for (Field field : type.getFields()) {
            result[index++] = new FieldValue(field, values[field.getIndex()]);
        }
        return result;
    }

    public MutableFieldValues setValue(Field field, Object value) throws InvalidParameter {
        if (field.getGlobType() != type) {
            throw new RuntimeException(field.getFullName() + " is not of type " + type.getName());
        }
        field.checkValue(value);
        values[field.getIndex()] = value;
        return this;
    }
}
