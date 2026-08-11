package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;

import java.util.BitSet;

public final class DefaultGlob extends AbstractDefaultGlob {
    // public, not private : a generated caller (globs-generate's AsmCallerGenerator.forDefaultGlob)
    // GETFIELDs the set BitSet straight out of another package, to walk the fields of a Glob without a
    // Field object, a getIndex() or a virtual call. Treat it as read-only from the outside.
    public final BitSet isSet;

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
}
