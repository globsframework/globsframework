package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetIntAccessor extends GlobGetAccessor {
    Integer get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

    default int get(Glob glob, int defaultValueIfNull) {
        Integer value = get(glob);
        return value == null ? defaultValueIfNull : value;
    }

    default int getNative(Glob glob) {
        return get(glob, 0);
    }

}
