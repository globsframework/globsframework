package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.LongField;
import org.globsframework.core.model.AbstractKey;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public class LongKeyField implements AbstractKey {
    private final LongField keyField;
    private final long value;
    private final int hashCode;

    public LongKeyField(LongField keyField, long value) {
        this.keyField = keyField;
        this.value = value;
        hashCode = computeHash();
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
        if (o instanceof LongKeyField otherSingleFieldKey) {
            return otherSingleFieldKey.getGlobType() == keyField.getGlobType()
                   && keyField.valueEqual(otherSingleFieldKey.value, value);
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }
        return keyField.getGlobType() == otherKey.getGlobType();
    }

    // optimized - do not use generated code
    public int hashCode() {
        return hashCode;
    }

    private int computeHash() {
        int hash = getGlobType().hashCode();
        hash = 31 * hash + Long.hashCode(value);
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
    On considere qu'une clé doit toujours avoir tous ses champs valorisé (au pire a null).
     */
    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }
}
