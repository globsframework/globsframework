package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.AbstractKey;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableKey;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.MissingInfo;

import java.util.Arrays;

public class TwoFieldKey extends AbstractKey {
    private final GlobType type;
    private Object value1;
    private Object value2;
    private int hashCode;

    private TwoFieldKey(GlobType type, Object value1, Object value2, int hashCode) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.hashCode = hashCode;
    }

    public TwoFieldKey(GlobType type) {
        this.type = type;
    }

    public TwoFieldKey(Field keyField1, Object value1,
                       Field keyField2, Object value2) throws MissingInfo {
        type = keyField1.getGlobType();
        Field[] keyFields = keyField1.getGlobType().getKeyFields();
        if (keyFields.length != 2) {
            throw new InvalidParameter("Cannot use a two-fields key for type " + keyField1.getGlobType() + " - " +
                    "key fields=" + Arrays.toString(keyFields));
        }
        setValue(keyField1, value1);
        setValue(keyField2, value2);
        hashCode = computeHash();
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

    protected Object doGetValue(Field field) {
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
        if (hashCode != 0) {
            return hashCode;
        }
        return hashCode = computeHash();
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

    public void reset() {
        value1 = value2 = null;
        hashCode = 0;
    }

    public MutableKey duplicateKey() {
        return new TwoFieldKey(type, value1, value2, hashCode);
    }

    public MutableKey setValue(Field field, Object value) throws ItemNotFound {
        FieldCheck.checkIsKeyOf(field, type);
        FieldCheck.checkValue(field, value);
        if (field.getKeyIndex() == 0) {
            value1 = value;
        } else {
            value2 = value;
        }
        return this;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }

}
