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

public class ThreeFieldKey extends AbstractKey {
    private final GlobType type;
    private Object value1;
    private Object value2;
    private Object value3;
    private int hashCode;

    public ThreeFieldKey(GlobType type) {
        this.type = type;
    }

    private ThreeFieldKey(GlobType type, Object value1, Object value2, Object value3, int hashCode) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.hashCode = hashCode;
    }

    public ThreeFieldKey(Field keyField1, Object value1,
                         Field keyField2, Object value2,
                         Field keyField3, Object value3) throws MissingInfo {
        Field[] keyFields = keyField1.getGlobType().getKeyFields();
        if (keyFields.length != 3) {
            throw new InvalidParameter("Cannot use a three-fields key for type " + keyField1.getGlobType() + " - " +
                    "key fields=" + Arrays.toString(keyFields));
        }
        type = keyField1.getGlobType();
        setValue(keyField1, value1);
        setValue(keyField2, value2);
        setValue(keyField3, value3);
        hashCode = computeHash();
    }

    public GlobType getGlobType() {
        return type;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        Field[] fields = type.getKeyFields();
        functor.process(fields[0], value1);
        functor.process(fields[1], value2);
        functor.process(fields[2], value3);
        return functor;
    }

    public int size() {
        return 3;
    }


    // optimized - do not use generated code
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof ThreeFieldKey otherSingleFieldKey) {
            Field[] keyFields = type.getKeyFields();
            return
                    type == otherSingleFieldKey.getGlobType() &&
                            keyFields[0].valueEqual(otherSingleFieldKey.value1, value1) &&
                            keyFields[1].valueEqual(otherSingleFieldKey.value2, value2) &&
                            keyFields[2].valueEqual(otherSingleFieldKey.value3, value3);
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }
        Field[] keyFields = type.getKeyFields();
        return type == otherKey.getGlobType()
                && keyFields[0].valueEqual(value1, otherKey.getValue(keyFields[0]))
                && keyFields[1].valueEqual(value2, otherKey.getValue(keyFields[1]))
                && keyFields[2].valueEqual(value3, otherKey.getValue(keyFields[2]));
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
        h = 31 * h + (value1 != null ? value1.hashCode() : 0);
        h = 31 * h + (value2 != null ? value2.hashCode() : 0);
        h = 31 * h + (value3 != null ? value3.hashCode() : 0);
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
                new FieldValue(fields[2], value3)
        };
    }

    public String toString() {
        Field[] fields = type.getKeyFields();
        return getGlobType().getName() + "[" +
                fields[0].getName() + "=" + value1 + ", " +
                fields[1].getName() + "=" + value2 + ", " +
                fields[2].getName() + "=" + value3 + "]";
    }

    protected Object doGetValue(Field field) {
        switch (field.getKeyIndex()) {
            case 0:
                return value1;
            case 1:
                return value2;
            case 2:
                return value3;
        }
        throw new InvalidParameter(field + " is not a key field");
    }

    public void reset() {
        value1 = value2 = value3 = null;
        hashCode = 0;
    }

    public MutableKey duplicateKey() {
        return new ThreeFieldKey(type, value1, value2, value3, hashCode);
    }

    public MutableKey setValue(Field field, Object value) throws ItemNotFound {
        FieldCheck.checkIsKeyOf(field, type);
        FieldCheck.checkValue(field, value);
        int index = field.getKeyIndex();
        if (index == 0) {
            value1 = value;
        } else if (index == 1) {
            value2 = value;
        } else {
            value3 = value;
        }
        return this;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }

}
