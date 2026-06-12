package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldValues;

import java.util.Arrays;

public class ManyFunctionalKeyBuilder implements FunctionalKeyBuilder {
    final GlobType type;
    final int hash;
    final int[] index;
    final Field[] fields;

    ManyFunctionalKeyBuilder(Field[] fields) {
        this.fields = fields;
        type = fields[0].getGlobType();
        index = new int[fields[0].getGlobType().getFieldCount()];
        Arrays.fill(index, -1);
        int i = 0;
        for (Field field : fields) {
            index[field.getIndex()] = i;
            ++i;
        }
        hash = type.getName().hashCode();
    }

    public GlobType getType() {
        return type;
    }

    public Field[] getFields() {
        return fields;
    }

    public FunctionalKey create(FieldValues fieldValues) {
        return new ManyFieldsMutableKey(this, fieldValues);
    }

    public FunctionalKey proxy(FieldValues fieldValues) {
        return create(fieldValues);
    }

    public MutableFunctionalKey create() {
        return new ManyFieldsMutableKey(this);
    }

    public String toString() {
        return "ManyFunctionalKeyBuilder{" +
               "fields=" + Arrays.toString(fields) +
               '}';
    }
}
