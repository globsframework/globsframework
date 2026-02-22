package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;

public final class DefaultGlob64 extends AbstractDefaultGlob {
    private long set;

    public DefaultGlob64(GlobType type) {
        super(type);
    }

    public void setSetAt(int index) {
        assert index < 64;
        set |= (1L << index);
    }

    public boolean isSetAt(int index) {
        assert index < 64;
        return (set & (1L << index)) != 0;
    }

    public void clearSetAt(int index) {
        assert index < 64;
        set &= ~(1L << index);
    }

    @Override
    void resetSet() {
        set = 0;
    }

}
