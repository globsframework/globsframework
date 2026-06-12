package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

import java.time.LocalDate;

public interface GlobGetDateAccessor extends GlobGetAccessor {
    LocalDate get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

}
