package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.*;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public class LongKeyField extends AbstractKey {
    private boolean isNull = true;
    private final Field keyField;
    private long value;

    public LongKeyField(Field keyField) {
        this.keyField = keyField;
    }

    public LongKeyField(Field keyField, boolean isNull, long value) {
        this.keyField = keyField;
        this.isNull = isNull;
        this.value = value;
    }

    public LongKeyField(Field field, Long value) {
        this.keyField = field;
        this.isNull = value == null;
        this.value = value == null ? 0 : value;
    }

    public GlobType getGlobType() {
        return keyField.getGlobType();
    }

    public <T extends FieldValues.Functor>
    T apply(T functor) throws Exception {
        functor.process(keyField, isNull ? null : value);
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
            return otherSingleFieldKey.getGlobType() == keyField.getGlobType() &&
                    (!otherSingleFieldKey.isNull && !isNull) ? keyField.valueEqual(otherSingleFieldKey.value, value) :
                    otherSingleFieldKey.isNull && isNull;
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }
        return keyField.getGlobType() == otherKey.getGlobType()
                && keyField.valueEqual(isNull ? null : value, otherKey.getValue(keyField));
    }

    // optimized - do not use generated code
    public int hashCode() {
        return computeHash();
    }

    private int computeHash() {
        int hash = getGlobType().hashCode();
        hash = 31 * hash + (isNull ? 0 : Long.hashCode(value));
        if (hash == 0) {
            hash = 31;
        }
        return hash;
    }

    public FieldValue[] toArray() {
        return new FieldValue[]{
                new FieldValue(keyField, isNull ? null : value),
        };
    }

    public String toString() {
        return getGlobType().getName() + "[" + keyField.getName() + "=" + (isNull ? "null" : value) + "]";
    }

    protected Object doGetValue(Field field) {
        if (field.getKeyIndex() == 0) {
            return isNull ? null : value;
        }
        throw new InvalidParameter(field + " is not a key field");
    }

    public MutableKey setValue(Field field, Object value) throws ItemNotFound {
        FieldCheck.checkIsKeyOf(field, getGlobType());
        isNull = value == null;
        this.value = value == null ? 0 : (long) value;
        return this;
    }

    public void reset() {
        isNull = true;
        this.value = 0;
    }

    public MutableKey duplicateKey() {
        return new LongKeyField(keyField, isNull, value);
    }

    /*
    On considere qu'une clé doit toujours avoir tous ses champs valorisé (au pire a null).
     */
    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }
}
