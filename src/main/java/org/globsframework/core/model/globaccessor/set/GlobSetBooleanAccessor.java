package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetBooleanAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, Boolean value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((Boolean) value));
    }

    default void setNative(MutableGlob glob, boolean value) {
        set(glob, value);
    }

}
