package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;

import java.util.Arrays;

import static org.globsframework.core.functional.impl.AbstractFieldValue.NULL_VALUE;

final class ThreeFunctionalKeyBuilder implements FunctionalKeyBuilder {
    final GlobType type;
    final Field field1;
    final Field field2;
    final Field field3;
    /**
     * Same seed as {@link ManyFunctionalKeyBuilder#hash}: {@link ThreeFieldsMutableKey#hashCode} has to give the hash a
     * {@link ManyFieldsMutableKey} gave on the same fields, since globs-shared writes it into its off-heap files.
     */
    final int hash;
    final Field[] fields;

    ThreeFunctionalKeyBuilder(Field field1, Field field2, Field field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        type = field1.getGlobType();
        fields = new Field[]{field1, field2, field3};
        hash = type.getName().hashCode();
    }

    public GlobType getType() {
        return type;
    }

    public Field[] getFields() {
        return fields;
    }

    public FunctionalKey create(FieldValues fieldValues) {
        return new ThreeFieldsMutableKey(this,
                fieldValues.isSet(field1) ? fieldValues.getValue(field1) : NULL_VALUE,
                fieldValues.isSet(field2) ? fieldValues.getValue(field2) : NULL_VALUE,
                fieldValues.isSet(field3) ? fieldValues.getValue(field3) : NULL_VALUE);
    }

    public FunctionalKey proxy(FieldValues fieldValues) {
        return create(fieldValues);
    }

    public MutableFunctionalKey create() {
        return new ThreeFieldsMutableKey(this);
    }

    public String toString() {
        return "ThreeFunctionalKeyBuilder{fields=" + Arrays.toString(fields) + '}';
    }
}
