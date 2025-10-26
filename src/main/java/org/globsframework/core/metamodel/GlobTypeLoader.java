package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.index.MultiFieldNotUniqueIndex;
import org.globsframework.core.metamodel.index.MultiFieldUniqueIndex;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.index.UniqueIndex;

public interface GlobTypeLoader {
    GlobTypeLoader load();

    GlobType getType();

    GlobTypeLoader defineUniqueIndex(UniqueIndex index, Field field);

    GlobTypeLoader defineNonUniqueIndex(NotUniqueIndex index, Field field);

    GlobTypeLoader defineMultiFieldUniqueIndex(MultiFieldUniqueIndex index, Field... fields);

    GlobTypeLoader defineMultiFieldNotUniqueIndex(MultiFieldNotUniqueIndex index, Field... fields);

    <T> GlobTypeLoader register(Class<T> klass, T t);

    <T> GlobTypeLoader register(Field field, Class<T> klass, T t);
}
