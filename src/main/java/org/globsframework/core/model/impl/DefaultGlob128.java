package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;

public final class DefaultGlob128 extends AbstractDefaultGlob {
    private long set1;
    private long set2;

    public DefaultGlob128(GlobType type) {
        super(type);
    }

    public void setSetAt(int index) {
        assert index < 128;
        if (index < 64) {
            set1 |= (1L << index);
        } else {
            set2 |= (1L << (index - 64));
        }
    }

    public boolean isSetAt(int index) {
        assert index < 128;
        if (index < 64) {
            return (set1 & (1L << index)) != 0;
        } else {
            return (set2 & (1L << (index - 64))) != 0;
        }
    }

    public void clearSetAt(int index) {
        assert index < 128;
        if (index < 64) {
            set1 &= ~(1L << index);
        } else {
            set2 &= ~(1L << (index - 64));
        }
    }

    @Override
    void resetSet() {
        set1 = 0;
        set2 = 0;
    }
}
