package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;

public interface GlobSetGlobArrayAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, Glob[] values);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((Glob[]) value));
    }
}
