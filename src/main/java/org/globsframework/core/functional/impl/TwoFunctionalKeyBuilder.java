package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;

import static org.globsframework.core.functional.impl.AbstractFieldValue.NULL_VALUE;

class TwoFunctionalKeyBuilder implements FunctionalKeyBuilder {
    final Field field1;
    final Field field2;
    private Field fields[];

    public TwoFunctionalKeyBuilder(Field field1, Field field2) {
        this.field1 = field1;
        this.field2 = field2;
        fields = new Field[]{field1, field2};
    }

    public GlobType getType() {
        return field1.getGlobType();
    }

    public Field[] getFields() {
        return fields;
    }

    public FunctionalKey create(FieldValues fieldValues) {
        return new TwoFieldsMutableKey(this,
                fieldValues.isSet(field1) ? fieldValues.getValue(field1): NULL_VALUE,
                fieldValues.isSet(field2) ? fieldValues.getValue(field2) : NULL_VALUE);
    }

    public FunctionalKey proxy(FieldValues fieldValues) {
        return create(fieldValues);
    }

    public MutableFunctionalKey create() {
        return new TwoFieldsMutableKey(this);
    }

    public String toString() {
        return "TwoFunctionalKeyBuilder{" +
                "field1=" + field1 +
                ", field2=" + field2 +
                '}';
    }
}
