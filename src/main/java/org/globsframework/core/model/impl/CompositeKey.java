package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.AbstractKey;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableKey;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.model.utils.FieldValueGetter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.MissingInfo;

import java.util.Arrays;

public class CompositeKey extends AbstractKey {
    private final GlobType type;
    private final Object[] values;
    private int hashCode;

    public CompositeKey(GlobType type, FieldValueGetter getter) {
        this.type = type;
        Field[] keyFields = type.getKeyFields();
        this.values = new Object[keyFields.length];
        for (Field field : keyFields) {
            if (!getter.contains(field)) {
                throw new MissingInfo("Field '" + field.getName() +
                        "' missing for identifying a Glob of type: " + type.getName());
            }
            values[field.getKeyIndex()] = getter.get(field);
        }
        hashCode = computeHash();
    }

    public CompositeKey(GlobType type) {
        this.type = type;
        values = new Object[type.getKeyFields().length];
    }

    private CompositeKey(GlobType type, Object[] globValues, int hashCode) {
        this.type = type;
        Field[] keyFields = type.getKeyFields();
        this.values = new Object[keyFields.length];
        for (Field field : keyFields) {
            values[field.getKeyIndex()] = globValues[field.getKeyIndex()];
        }
        this.hashCode = hashCode;
    }

    public GlobType getGlobType() {
        return type;
    }

    public int size() {
        return type.getKeyFields().length;
    }

    public <T extends Functor>
    T apply(T functor) throws Exception {
        Field[] fields = type.getKeyFields();
        for (Field field : fields) {
            functor.process(field, values[field.getKeyIndex()]);
        }
        return functor;
    }

    // optimized - do not use generated code
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Key otherKey)) {
            return false;
        }

        if (type != otherKey.getGlobType()) {
            return false;
        }

        for (Field field : type.getKeyFields()) {
            if (!field.valueEqual(getValue(field), otherKey.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    // optimized - do not use generated code
    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }
        return hashCode = computeHash();
    }

    private int computeHash() {
        int hashCode = type.hashCode();
        for (Field keyField : type.getKeyFields()) {
            Object value = getValue(keyField);
            hashCode = 31 * hashCode + (value != null ? keyField.valueHash(value) : 0);
        }
        if (hashCode == 0) {
            hashCode = 31;
        }
        return hashCode;
    }

    // Overwritten so that fields are always in the same order
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type.getName());
        builder.append('[');
        Field[] fields = type.getKeyFields();
        int i = 0;
        for (Field field : fields) {
            builder.append(field.getName());
            builder.append('=');
            builder.append(getValue(field));
            i++;
            if (i < fields.length) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public FieldValue[] toArray() {
        Field[] keyFields = type.getKeyFields();
        FieldValue[] array = new FieldValue[keyFields.length];
        int index = 0;
        for (Field field : keyFields) {
            array[index] = new FieldValue(field, values[index]);
            index++;
        }
        return array;
    }

    protected Object doGetValue(Field field) {
        return values[field.getKeyIndex()];
    }

    public void reset() {
        Arrays.setAll(values, i -> null);
        hashCode = 0;
    }

    public MutableKey duplicateKey() {
        return new CompositeKey(type, values, hashCode);
    }

    public MutableKey setValue(Field field, Object value) throws ItemNotFound {
        FieldCheck.checkIsKeyOf(field, type, value);
        values[field.getKeyIndex()] = value;
        return this;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return true;
    }
}
