package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;

public final class DefaultGlob64 extends AbstractDefaultGlob {
    // public, not private : a generated caller (globs-generate's AsmCallerGenerator.forDefaultGlob)
    // GETFIELDs the set mask straight out of another package, to walk the fields of a Glob without a
    // Field object, a getIndex() or a virtual call. Treat it as read-only from the outside.
    public long set;

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

}
