package org.globsframework.core.streams;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.streams.accessors.Accessor;

import java.io.Closeable;
import java.util.Collection;

public interface GlobStream extends Closeable {

    boolean next();

    Collection<Field> getFields();

    Accessor getAccessor(Field field);

    void close();
}
