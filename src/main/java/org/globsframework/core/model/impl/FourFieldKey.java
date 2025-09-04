package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.AbstractKey;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableKey;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.MissingInfo;

import java.util.Arrays;

public class FourFieldKey extends AbstractKey {
    private final GlobType type;
    private Object value1;
    private Object value2;
    private Object value3;
    private Object value4;
    private int hashCode;

    private FourFieldKey(GlobType type, Object value1, Object value2, Object value3, Object value4, int hashCode) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.hashCode = hashCode;
    }

    public FourFieldKey(GlobType type) {
        this.type = type;
    }

    public FourFieldKey(Field keyField1, Object value1,
                        Field keyField2, Object value2,
                        Field keyField3, Object value3,
                        Field keyField4, Object value4) throws MissingInfo {
        type = keyField1.getGlobType();

        Field[] keyFields = keyField1.getGlobType().getKeyFields();
        if (keyFields.length != 4) {
            throw new InvalidParameter("Cannot use a three-fields key for type " + keyField1.getGlobType() + " - " +
                    "key fields=" + Arrays.toString(keyFields));
        }
        setValue(keyField1, value1);
        setValue(keyField2, value2);
        setValue(keyField3, value3);
        setValue(keyField4, value4);
        hashCode = computeHash();
    }

    public GlobType getGlobType() {
        return type;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        Field[] fields = type.getFields();
        functor.process(fields[0], value1);
        functor.process(fields[1], value2);
        functor.process(fields[2], value3);
        functor.process(fields[3], value4);
        return functor;
    }

    public <T extends Functor> T safeApply(T functor) {
        try {
            Field[] fields = type.getFields();
            functor.process(fields[0], value1);
            functor.process(fields[1], value2);
            functor.process(fields[2], value3);
            functor.process(fields[3], value4);
            return functor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return 4;
    }

    protected Object doGetValue(Field field) {
        switch (field.getKeyIndex()) {
            case 0:
                return value1;
            case 1:
                return value2;
            case 2:
                return value3;
            case 3:
                return value4;
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
        if (o instanceof FourFieldKey otherSingleFieldKey) {
            Field[] keyFields = getGlobType().getKeyFields();
            return type == otherSingleFieldKey.getGlobType() &&
                    keyFields[0].valueEqual(otherSingleFieldKey.value1, value1) &&
                    keyFields[1].valueEqual(otherSingleFieldKey.value2, value2) &&
                    keyFields[2].valueEqual(otherSingleFieldKey.value3, value3) &&
                    keyFields[3].valueEqual(otherSingleFieldKey.value4, value4);
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }
        Field[] keyFields = type.getKeyFields();
        return type == otherKey.getGlobType()
                && keyFields[0].valueEqual(value1, otherKey.getValue(keyFields[0]))
                && keyFields[1].valueEqual(value2, otherKey.getValue(keyFields[1]))
                && keyFields[2].valueEqual(value3, otherKey.getValue(keyFields[2]))
                && keyFields[3].valueEqual(value4, otherKey.getValue(keyFields[3]));
    }

    // optimized - do not use generated code
    public int hashCode() {
        return hashCode;
    }

    private int computeHash() {
        Field[] fields = type.getKeyFields();
        int h = type.hashCode();
        h = 31 * h + (value1 != null ? fields[0].valueHash(value1) : 0);
        h = 31 * h + (value2 != null ? fields[1].valueHash(value2) : 0);
        h = 31 * h + (value3 != null ? fields[2].valueHash(value3) : 0);
        h = 31 * h + (value4 != null ? fields[3].valueHash(value4) : 0);
        if (h == 0) {
            h = 31;
        }
        return h;
    }

    public FieldValue[] toArray() {
        Field[] fields = type.getFields();
        return new FieldValue[]{
                new FieldValue(fields[0], value1),
                new FieldValue(fields[1], value2),
                new FieldValue(fields[2], value3),
                new FieldValue(fields[3], value4)
        };
    }

    public String toString() {
        Field[] fields = type.getFields();
        return getGlobType().getName() + "[" +
                fields[0].getName() + "=" + value1 + ", " +
                fields[1].getName() + "=" + value2 + ", " +
                fields[2].getName() + "=" + value3 + ", " +
                fields[3].getName() + "=" + value4 + "]";
    }

    public void reset() {
        value1 = value2 = value3 = value4 = null;
        hashCode = 0;
    }

    public MutableKey duplicateKey() {
        return new FourFieldKey(type, value1, value2, value3, value4, hashCode);
    }

    public MutableKey setValue(Field field, Object value) throws ItemNotFound {
        switch (field.getKeyIndex()) {
            case 0:
                value1 = value;
                break;
            case 1:
                value2 = value;
                break;
            case 2:
                value3 = value;
                break;
            case 3:
                value4 = value;
                break;
        }
        return null;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }

}
