package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.links.Link;

public interface Glob extends FieldValues {
    GlobType getType();

    Key getKey();

    Key getTargetKey(Link link);

    boolean matches(FieldValues values);

    boolean matches(FieldValue... values);

    FieldValues getValues();

    MutableGlob duplicate();

    void reserve(int key) throws ReservationException;

    // return true if the reservation was successful and false if it was already released by the same key.
    boolean release(int key) throws ReservationException;

    void unReserve();

    boolean isReserved();

    boolean isReservedBy(int key);

    default Key getNewKey() {
        return KeyBuilder.createFromValues(getType(), this);
    }

    void checkWasReservedBy(int key);

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
