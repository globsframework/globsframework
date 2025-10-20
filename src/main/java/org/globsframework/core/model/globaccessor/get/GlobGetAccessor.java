package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetAccessor {

    boolean isSet(Glob glob);

    Object getValue(Glob glob);
}
