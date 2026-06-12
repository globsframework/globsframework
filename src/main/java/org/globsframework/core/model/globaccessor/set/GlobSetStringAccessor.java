package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

public interface GlobSetStringAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, String value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((String) value));
    }

}
