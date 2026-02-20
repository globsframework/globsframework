package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.ReservationException;

import java.util.BitSet;

public final class DefaultGlob extends AbstractDefaultGlob {
    private final BitSet isSet;

    public DefaultGlob(GlobType type) {
        super(type);
        isSet = new BitSet(type.getFieldCount());
    }

    public void setSetAt(int index) {
        isSet.set(index);
    }

    public boolean isSetAt(int index) {
        return isSet.get(index);
    }

    public void clearSetAt(int index) {
        isSet.clear(index);
    }


    @Override
    void resetSet() {
        isSet.clear();
    }
}
