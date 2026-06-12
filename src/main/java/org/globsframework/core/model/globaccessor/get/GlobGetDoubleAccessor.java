package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetDoubleAccessor extends GlobGetAccessor {
    Double get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

    default double get(Glob glob, double defaultValueIfNull) {
        Double value = get(glob);
        return value == null ? defaultValueIfNull : value;
    }

    default double getNative(Glob glob) {
        return get(glob, 0.);
    }

}
