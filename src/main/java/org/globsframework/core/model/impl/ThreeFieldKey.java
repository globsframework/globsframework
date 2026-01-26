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

public class ThreeFieldKey implements AbstractKey {
    private final GlobType type;
    private final Object value1;
    private final Object value2;
    private final Object value3;
    private final int hashCode;

    public ThreeFieldKey(Field keyField1, Object value1,
                         Field keyField2, Object value2,
                         Field keyField3, Object value3) throws MissingInfo {
        type = keyField1.getGlobType();
        Field[] keyFields = type.getKeyFields();
        if (keyFields.length != 3) {
            throw new InvalidParameter("Cannot use a three-fields key for type " + type + " - " +
                                       "key fields=" + Arrays.toString(keyFields));
        }
        this.value1 = get(0, keyField1, keyField2, keyField3, value1, value2, value3);
        this.value2 = get(1, keyField1, keyField2, keyField3, value1, value2, value3);
        this.value3 = get(2, keyField1, keyField2, keyField3, value1, value2, value3);
        hashCode = computeHash();
    }

    private Object get(int wanted, Field keyField1, Field keyField2, Field keyField3, Object value1, Object value2, Object value3) {
        if (keyField1.getKeyIndex() == wanted) {
            return value1;
        }
        if (keyField2.getKeyIndex() == wanted) {
            return value2;
        }
        if (keyField3.getKeyIndex() == wanted) {
            return value3;
        }
        throw new InvalidParameter("Cannot find key field " + wanted + " in " + type);
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
        return hashCode;
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

    public Object doGetValue(Field field) {
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

    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }

}
