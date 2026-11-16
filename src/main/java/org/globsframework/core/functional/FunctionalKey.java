package org.globsframework.core.functional;

import org.globsframework.core.model.FieldValues;

import org.globsframework.core.metamodel.fields.Field;

public interface FunctionalKey extends FieldValues {
    FunctionalKeyBuilder getBuilder();

    default boolean matches(FieldValues other) {
        for (Field field : getBuilder().getFields()) {
            if (!field.valueEqual(getValue(field), other.getValue(field))) {
                return false;
            }
        }
        return true;
    }
}
