package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.Annotations;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.container.specific.HashEmptyGlobContainer;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Collection;
import java.util.stream.Stream;

public interface AbstractDefaultAnnotations extends Annotations {

    HashContainer<Key, Glob> getAnnotations();

    default Stream<Glob> streamAnnotations() {
        return getAnnotations().stream();
    }

    default Stream<Glob> streamAnnotations(GlobType type) {
        return getAnnotations().stream().filter(glob -> glob.getType() == type);
    }

    default boolean hasAnnotation(Key key) {
        return getAnnotations().containsKey(key);
    }

    default Glob getAnnotation(Key key) {
        Glob annotation = getAnnotations().get(key);
        if (annotation == null) {
            throw new ItemNotFound(key.toString() + " on " + toString());
        }
        return annotation;
    }

    default Glob findAnnotation(Key key) {
        return getAnnotations().get(key);
    }

}
