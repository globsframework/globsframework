package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.Ref;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Optional;
import java.util.stream.Stream;

public interface Annotations {
    Stream<Glob> streamAnnotations();

    default Stream<Glob> streamAnnotations(GlobType type) {
        return streamAnnotations().filter(glob -> glob.getType().equals(type));
    }

    boolean hasAnnotation(Key key);

    Glob findAnnotation(Key key);

    default Glob getAnnotation(Key key) {
        Glob annotation = findAnnotation(key);
        if (annotation == null) {
            throw new ItemNotFound(key.toString());
        }
        return annotation;
    }

    default <T> T getValueOrDefault(Key key, Field field, T defaultValue) {
        Glob annotation = findAnnotation(key);
        if (annotation != null) {
            return (T) annotation.getValue(field);
        }
        return defaultValue;
    }

    default Optional<Glob> findOptAnnotation(Key key) {
        return Optional.ofNullable(findAnnotation(key));
    }

    default boolean findAnnotation(Key key, Ref<Glob> result) {
        result.set(findAnnotation(key));
        return result.get() != null;
    }

}
