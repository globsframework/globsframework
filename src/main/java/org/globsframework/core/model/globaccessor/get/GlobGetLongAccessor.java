package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetLongAccessor extends GlobGetAccessor {

    Long get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

    default long get(Glob glob, long defaultValueIfNull) {
        Long value = get(glob);
        if (value == null) {
            return defaultValueIfNull;
        } else {
            return value;
        }
    }

    default long getNative(Glob glob) {  // return the field default value if one is available or 0
        return get(glob, 0l);
    }

}
