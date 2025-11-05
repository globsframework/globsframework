package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

public interface GlobGetIntAccessor extends GlobGetAccessor {
    int get(Glob glob, int defaultValueIfNull); // return defaultValueIfNull only if

    int getNative(Glob glob); // return  0 if null

    Integer get(Glob glob);
}
