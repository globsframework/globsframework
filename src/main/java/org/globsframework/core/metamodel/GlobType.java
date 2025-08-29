package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldVisitor;
import org.globsframework.core.metamodel.fields.FieldVisitorWithContext;
import org.globsframework.core.metamodel.impl.DefaultValuesFieldVisitor;
import org.globsframework.core.metamodel.index.Index;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.model.GlobFactory;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public interface GlobType extends MutableAnnotations {

    String getName();

    Field getField(String name) throws ItemNotFound;

    <T extends Field> T getTypedField(String name) throws ItemNotFound;

    Field findField(String name);

    default Optional<Field> findOptField(String name) {
        return Optional.ofNullable(findField(name));
    }

    boolean hasField(String name);

    Field[] getFields();

    Stream<Field> streamFields();

    Field getField(int index);

    int getFieldCount();

    Field[] getKeyFields();

    Field findFieldWithAnnotation(Key key);

    Field getFieldWithAnnotation(Key key) throws ItemNotFound;

    Collection<Field> getFieldsWithAnnotation(Key key);

    Collection<Index> getIndices();

    GlobFactory getGlobFactory();

    <T> T getRegistered(Class<T> klass);

    <T> T getRegistered(Class<T> klass, T NULL);

    String describe();

//    GlobGetAccessor getAccessor(Field field);

    default MutableGlob instantiate() {
        return instantiate(null);
    }

    default MutableGlob instantiate(Object context) {
        return getGlobFactory().create(context);
    }

    default MutableGlob instantiateWithDefaults() {
        return instantiateWithDefaults(null);
    }

    default MutableGlob instantiateWithDefaults(Object context) {
        MutableGlob glob = instantiate(context);
        FieldVisitorWithContext<MutableGlob> visitor = DefaultValuesFieldVisitor.INSTANCE;
        streamFields().forEach(field -> field.safeAccept(visitor, glob));
        return glob;
    }

    default <T extends FieldVisitor> T accept(T visitor) throws Exception {
        return getGlobFactory().accept(visitor);
    }

    default <T extends FieldVisitor> T saveAccept(T visitor) {
        try {
            return getGlobFactory().accept(visitor);
        } catch (Exception e) {
            throw new RuntimeException("for visitor : " + visitor.getClass().getName(), e);
        }
    }

    default boolean hasKeys() {
        return getKeyFields().length != 0;
    }

    Comparator<Key> sameKeyComparator();

    abstract class Property<T> {
        static AtomicInteger at = new AtomicInteger(0);
        private final int index = at.getAndIncrement();

        public int getIndex() {
            return index;
        }

        public abstract T build(GlobType globType);
    }

    <T> T get(Property<T> property);
}
