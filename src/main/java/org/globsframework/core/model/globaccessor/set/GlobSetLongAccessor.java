package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetLongAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, Long value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((Long) value));
    }

    default void setNative(MutableGlob glob, long value) {
        set(glob, value);
    }

}
