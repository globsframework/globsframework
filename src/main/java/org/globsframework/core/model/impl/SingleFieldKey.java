package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.*;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.MissingInfo;

import java.util.Arrays;

public class SingleFieldKey implements AbstractKey {
    private final Field keyField;
    private final Object value;
    private final int hashCode;

    public SingleFieldKey(Field field, Object value) throws MissingInfo {
        FieldCheck.checkIsKeyOf(field, field.getGlobType(), value);
        this.keyField = field;
        this.value = value;
        hashCode = computeHash();
    }

    public SingleFieldKey(GlobType type, Object value) throws InvalidParameter {
        this(getKeyField(type), value);
    }

    public static Field getKeyField(GlobType type) throws InvalidParameter {
        Field[] keyFields = type.getKeyFields();
        if (keyFields.length != 1) {
            throw new InvalidParameter("Cannot use a single field key for type " + type + " - " +
                    "key fields=" + Arrays.toString(keyFields));
        }
        return keyFields[0];
    }

    public GlobType getGlobType() {
        return keyField.getGlobType();
    }

    public <T extends FieldValues.Functor>
    T apply(T functor) throws Exception {
        functor.process(keyField, value);
        return functor;
    }

    public int size() {
        return 1;
    }

    // optimized - do not use generated code
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof SingleFieldKey otherSingleFieldKey) {
            return otherSingleFieldKey.keyField == keyField &&
                    keyField.valueEqual(otherSingleFieldKey.value, value);
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }
        return keyField.getGlobType() == otherKey.getGlobType()
                && keyField.valueEqual(value, otherKey.getValue(keyField));
    }

    public int hashCode() {
        return hashCode;
    }

    private int computeHash() {
        int hash = getGlobType().hashCode();
        hash = 31 * hash + (value != null ? keyField.valueHash(value) : 0);
        if (hash == 0) {
            hash = 31;
        }
        return hash;
    }

    public FieldValue[] toArray() {
        return new FieldValue[]{
                new FieldValue(keyField, value),
        };
    }

    public String toString() {
        return getGlobType().getName() + "[" + keyField.getName() + "=" + value + "]";
    }

    public Object doGetValue(Field field) {
        if (field.getKeyIndex() == 0) {
            return value;
        }
        throw new InvalidParameter(field + " is not a key field");
    }

    /*
    On considere qu'une clé doit toujours avoir tous ses champs valorisé (au pure a null).
     */
    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }

}
