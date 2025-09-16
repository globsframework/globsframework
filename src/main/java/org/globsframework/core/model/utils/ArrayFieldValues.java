package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.impl.AbstractFieldValues;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Arrays;

public class ArrayFieldValues implements AbstractFieldValues {
    private FieldValue[] values;

    public ArrayFieldValues(FieldValue[] values) {
        this.values = values;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        for (FieldValue value : values) {
            if (value.getField().equals(field)) {
                return true;
            }
        }
        return false;
    }

    public Object doCheckedGet(Field field) {
        for (FieldValue value : values) {
            if (value.getField().equals(field)) {
                return value.getValue();
            }
        }
        return null;
    }

    public boolean contains(Field field) {
        for (FieldValue value : values) {
            if (value.getField().equals(field)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return values.length;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (FieldValue value : values) {
            value.getField().acceptValue(functor, value.getValue());
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        for (FieldValue value : values) {
            value.getField().acceptValue(functor, value.getValue(), ctx);
        }
        return functor;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        for (FieldValue value : values) {
            functor.process(value.getField(), value.getValue());
        }
        return functor;
    }

    public FieldValue[] toArray() {
        return values;
    }

    public String toString() {
        return Arrays.toString(values);
    }
}
