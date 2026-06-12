package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetIntAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, Integer value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((Integer) value));
    }

    default void setNative(MutableGlob glob, int value) {
        set(glob, value);
    }

}
