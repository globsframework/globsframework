package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.links.Link;

public interface Glob extends FieldValues {
    GlobType getType();

    Key getKey();

    Key getTargetKey(Link link);

    boolean matches(FieldValues values);

    boolean matches(FieldValue... values);

    FieldValues getValues();

    MutableGlob duplicate();

    default Key getNewKey() {
        return KeyBuilder.createFromValues(getType(), this);
    }

    default boolean same(Glob glob) {
        if (glob == this) {
            return true;
        }
        if (glob == null) {
            return false;
        }
        for (Field field : getType().getFields()) {
            if (!field.valueEqual(getValue(field), glob.getValue(field))) {
                return false;
            }
        }
        return true;
    }
}
