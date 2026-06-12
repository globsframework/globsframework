package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

import java.time.ZonedDateTime;

public interface GlobGetDateTimeAccessor extends GlobGetAccessor {
    ZonedDateTime get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

}
