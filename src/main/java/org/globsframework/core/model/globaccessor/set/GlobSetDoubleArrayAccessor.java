package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetDoubleArrayAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, double[] value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((double[]) value));
    }

}
