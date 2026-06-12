package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;

import static org.globsframework.core.functional.impl.AbstractFieldValue.NULL_VALUE;

public class ZeroFunctionalKeyBuilder implements FunctionalKeyBuilder {
    private static final Field[] FIELDS = new Field[0];
    final int hash;
    final GlobType type;

    public ZeroFunctionalKeyBuilder(GlobType type) {
        this.type = type;
        hash = type.getName().hashCode();
    }

    public GlobType getType() {
        return type;
    }

    public Field[] getFields() {
        return FIELDS;
    }

    public FunctionalKey create(FieldValues fieldValues) {
        return new ZeroFieldMutableKey(this);
    }

    public FunctionalKey proxy(FieldValues fieldValues) {
        return create(fieldValues);
    }

    public MutableFunctionalKey create() {
        return new ZeroFieldMutableKey(this);
    }

    @Override
    public String toString() {
        return "ZeroFunctionalKeyBuilder{" +
               "type=" + type.getName() +
               '}';
    }
}
