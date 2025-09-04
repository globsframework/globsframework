package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
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

    void release(int key) throws ReservationException;

    boolean isReserved();

    boolean isReservedBy(int key);

    default Key getNewKey() {
        return KeyBuilder.createFromValues(getType(), this);
    }

}
