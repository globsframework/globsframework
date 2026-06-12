package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetLongArrayAccessor extends GlobGetAccessor {
    long[] get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

}
