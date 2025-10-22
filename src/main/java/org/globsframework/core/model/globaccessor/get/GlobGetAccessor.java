package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetAccessor {

    Object getValue(Glob glob);

    boolean isSet(Glob glob);

    boolean isNull(Glob glob);
}
