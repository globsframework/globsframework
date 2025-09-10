package org.globsframework.core.model.cache;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;

public interface GlobsCache {

    MutableGlob newGlob(GlobType globType, int id);

    void release(Glob glob, int id);
}
