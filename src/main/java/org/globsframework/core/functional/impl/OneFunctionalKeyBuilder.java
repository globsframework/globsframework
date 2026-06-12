package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;

import static org.globsframework.core.functional.impl.AbstractFieldValue.NULL_VALUE;

final class OneFunctionalKeyBuilder implements FunctionalKeyBuilder {
    final GlobType type;
    final Field field;
    final int hash;
    final Field[] fields;

    public OneFunctionalKeyBuilder(Field field) {
        this.field = field;
        this.type = field.getGlobType();
        fields = new Field[]{field};
        hash = type.getName().hashCode() * 31;
    }

    public GlobType getType() {
        return type;
    }

    public Field[] getFields() {
        return fields;
    }

    public FunctionalKey create(FieldValues fieldValues) {
        return new OneFieldMutableKey(this,
                fieldValues.isSet(field) ? fieldValues.getValue(field) : NULL_VALUE);
    }

    public FunctionalKey proxy(FieldValues fieldValues) {
        return create(fieldValues);
    }

    public MutableFunctionalKey create() {
        return new OneFieldMutableKey(this);
    }

    public String toString() {
        return "OneFunctionalKeyBuilder{" +
               "field=" + field +
               '}';
    }
}
