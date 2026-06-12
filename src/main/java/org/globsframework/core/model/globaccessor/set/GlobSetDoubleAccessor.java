package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetDoubleAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, Double value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((Double) value));
    }

    default void setNative(MutableGlob glob, double value) {
        set(glob, value);
    }

}
