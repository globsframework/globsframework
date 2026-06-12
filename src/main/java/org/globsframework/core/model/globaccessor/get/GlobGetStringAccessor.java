package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetStringAccessor extends GlobGetAccessor {
    String get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

}
