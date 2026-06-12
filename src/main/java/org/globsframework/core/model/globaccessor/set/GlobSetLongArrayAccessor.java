package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetLongArrayAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, long[] value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((long[]) value));
    }

}
