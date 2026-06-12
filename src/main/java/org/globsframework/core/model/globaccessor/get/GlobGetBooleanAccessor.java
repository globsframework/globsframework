package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetBooleanAccessor extends GlobGetAccessor {

    Boolean get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

    default boolean get(Glob glob, boolean defaultValueIfNull) {
        Boolean value = get(glob);
        return value == null ? defaultValueIfNull : value;
    }

    default boolean getNative(Glob glob) {
        return get(glob, Boolean.FALSE);
    }

}
