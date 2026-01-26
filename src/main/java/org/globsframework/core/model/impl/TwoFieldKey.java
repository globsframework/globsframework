package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.AbstractKey;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.MissingInfo;

import java.util.Arrays;

public class TwoFieldKey implements AbstractKey {
    private final GlobType type;
    private final Object value1;
    private final Object value2;
    private final int hashCode;

    public TwoFieldKey(Field keyField1, Object value1,
                       Field keyField2, Object value2) throws MissingInfo {
        type = keyField1.getGlobType();
        Field[] keyFields = keyField1.getGlobType().getKeyFields();
        if (keyFields.length != 2) {
            throw new InvalidParameter("Cannot use a two-fields key for type " + keyField1.getGlobType() + " - " +
                                       "key fields=" + Arrays.toString(keyFields));
        }
        this.value1 = get(0, keyField1, keyField2, value1, value2);
        this.value2 = get(1, keyField1, keyField2, value1, value2);
        hashCode = computeHash();
    }

    private Object get(int wanted, Field keyField1, Field keyField2, Object value1, Object value2) {
        if (keyField1.getKeyIndex() == wanted) {
            return value1;
        }
        if (keyField2.getKeyIndex() == wanted) {
            return value2;
        }
        throw new InvalidParameter("Cannot find key field " + wanted + " in " + type);
    }

    public GlobType getGlobType() {
        return type;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        Field[] keyFields = type.getKeyFields();
        functor.process(keyFields[0], value1);
        functor.process(keyFields[1], value2);
        return functor;
    }


    public int size() {
        return 2;
    }

    public Object doGetValue(Field field) {
        switch (field.getKeyIndex()) {
            case 0:
                return value1;
            case 1:
                return value2;
        }
        throw new InvalidParameter(field + " is not a key field");
    }

    // optimized - do not use generated code
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof TwoFieldKey twoFieldKey) {
            Field[] keyFields = type.getKeyFields();
            return type == twoFieldKey.getGlobType() &&
                   keyFields[0].valueEqual(twoFieldKey.value1, value1) &&
                   keyFields[1].valueEqual(twoFieldKey.value2, value2);
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }
        Field[] keyFields = type.getKeyFields();
        return type == otherKey.getGlobType()
               && keyFields[0].valueEqual(value1, otherKey.getValue(keyFields[0]))
               && keyFields[1].valueEqual(value2, otherKey.getValue(keyFields[1]));
    }

    // optimized - do not use generated code
    public int hashCode() {
        return hashCode;
    }

    private int computeHash() {
        int h = type.hashCode();
        h = 31 * h + (value1 == null ? 31 : value1.hashCode());
        h = 31 * h + (value2 == null ? 31 : value2.hashCode());
        if (h == 0) {
            h = 31;
        }
        return h;
    }

    public FieldValue[] toArray() {
        Field[] fields = type.getKeyFields();
        return new FieldValue[]{
                new FieldValue(fields[0], value1),
                new FieldValue(fields[1], value2),
        };
    }

    public String toString() {
        Field[] fields = type.getKeyFields();
        return getGlobType().getName() + "[" +
               fields[0].getName() + "=" + value1 + ", " +
               fields[1].getName() + "=" + value2 + "]";
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }
}
