package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.ReservationException;

public final class DefaultGlob32 extends AbstractDefaultGlob {
    private int set;

    public DefaultGlob32(GlobType type) {
        super(type);
    }

    public void setSetAt(int index) {
        set |= (1 << index);
    }

    public boolean isSetAt(int index) {
        return (set & (1 << index)) != 0;
    }

    public void clearSetAt(int index) {
        set &= ~(1 << index);
    }

    public void resetSet() {
        set = 0;
    }

}
