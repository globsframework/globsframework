package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;

public interface GlobInstantiator {
    MutableGlob newGlob(GlobType globType);
}
