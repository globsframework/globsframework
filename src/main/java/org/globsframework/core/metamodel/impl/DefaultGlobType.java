package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.index.Index;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.metamodel.utils.MutableGlobType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobFactory;
import org.globsframework.core.model.GlobFactoryService;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.container.specific.HashEmptyGlobContainer;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.TooManyItems;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultGlobType
        implements AbstractDefaultAnnotations, MutableGlobType, MutableAnnotations {
    public static final String[] EMPTY_SCOPE = new String[0];
    public static final Object[] EMPTY_PROP = new Object[0];
    private static final Field[] EMPTY_FIELDS = new Field[0];
    private final Field[] fields;
    private final Field[] keyFields;
    private final GlobFactory globFactory;
    private final Supplier<Comparator<Key>> keyComparator;
    private final String name;
    private final Map<String, Field> fieldsByName;
    private final Map<String, Index> indices;
    private final Map<Class<?>, Object> registered;
    private final HashContainer<Key, Glob> annotations;
    private Object[] properties = EMPTY_PROP;

    public DefaultGlobType(String name, Map<String, Field> fieldsByName, Map<Class<?>, Object> registered,
                           List<Glob> annotations, Map<String, Index> indices, int keyIndex) {
        this.name = name;
        fields = new Field[fieldsByName.size()];
        this.registered = registered != null ? registered : Map.of();
        this.indices = indices != null ? indices : Map.of();
        if (keyIndex > 0) {
            keyFields = new Field[keyIndex];
        } else {
            keyFields = EMPTY_FIELDS;
        }
        int countF = 0;
        int countK = 0;
        for (Map.Entry<String, Field> stringFieldEntry : fieldsByName.entrySet()) {
            final Field f = stringFieldEntry.getValue();
            final int i = f.getIndex();
            if (fields[i] != null) {
                throw new RuntimeException("Duplicate field at same index " + i + " => " + f.getName() + " in type " + name);
            }
            countF++;
            fields[i] = f;
            if (fields[i].isKeyField()) {
                final int ki = f.getKeyIndex();
                if (keyFields[ki] != null) {
                    throw new RuntimeException("Duplicate key field at same index " + ki + " => " + f.getName() + " in type " + name);
                }
                countK++;
                keyFields[ki] = f;
            }
        }
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field == null) {
                throw new RuntimeException("Field at index " + i + "  was not initialized for type " + name);
            }
        }
        if (countF != fields.length) {
            throw new RuntimeException("Not all fields were initialized for type " + name);
        }
        for (int i = 0; i < keyFields.length; i++) {
            Field keyField = keyFields[i];
            if (keyField == null) {
                throw new RuntimeException("KeyField at index " + i + "  was not initialized for type " + name);
            }
        }
        if (countK != keyFields.length) {
            throw new RuntimeException("Not all keyFields were initialized for type " + name);
        }
        this.fieldsByName = fieldsByName; //StableValue.map(fieldsByName.keySet(), fieldsByName::get);
        globFactory = GlobFactoryService.Builder.getBuilderFactory().getFactory(this);
        keyComparator = new UnsafeSupplier<>(() ->
        {
            Comparator<Key> cmp = null;
            for (Field keyField : keyFields) {
                if (cmp == null) {
                    cmp = Comparator.comparing(key -> (Comparable) key.getValue(keyField));
                } else {
                    cmp = cmp.thenComparing(key -> (Comparable) key.getValue(keyField));
                }
            }
            return cmp;
        });
        if (annotations == null || annotations.isEmpty()) {
            this.annotations = HashEmptyGlobContainer.Helper.allocate(0);
        } else {
            this.annotations = HashEmptyGlobContainer.Helper.allocate(annotations.size());
            for (Glob annotation : annotations) {
                this.annotations.put(annotation.getKey(), annotation);
            }
        }
    }

    public int getFieldCount() {
        return fields.length;
    }

    public Field getField(String name) throws ItemNotFound {
        Field field = fieldsByName.get(name);
        if (field == null) {
            throw new ItemNotFound("Field '" + name + "' not found in type: " + this.name + " got " + fieldsByName.keySet());
        }
        return field;
    }

    public <T extends Field> T getTypedField(String name) throws ItemNotFound {
        return (T) getField(name);
    }

    public boolean hasField(String name) {
        return findField(name) != null;
    }

    public Field findField(String name) {
        return fieldsByName.get(name);
    }

    public Field[] getFields() {
        return fields;
    }

    public Stream<Field> streamFields() {
        return Arrays.stream(fields);
    }

    public Field getField(int index) {
        return fields[index];
    }

    public String getName() {
        return name;
    }

    public Field[] getKeyFields() {
        return keyFields;
    }

    public Field getFieldWithAnnotation(Key key) throws ItemNotFound {
        Field foundField = findFieldWithAnnotation(key);
        if (foundField != null) {
            return foundField;
        }
        throw new ItemNotFound("no field found with " + key + " under " + this);
    }

    public Field findFieldWithAnnotation(Key key) {
        Field foundField = null;
        for (Field field : fields) {
            if (field.hasAnnotation(key)) {
                if (foundField != null) {
                    throw new TooManyItems("Found multiple field with " + key + " => " + field + " and " + foundField);
                }
                foundField = field;
            }
        }
        return foundField;
    }

    public Collection<Field> getFieldsWithAnnotation(Key key) {
        List<Field> annotations = new ArrayList<>();
        for (Field field : fields) {
            if (field.hasAnnotation(key)) {
                annotations.add(field);
            }
        }
        return annotations;
    }

    @Override
    public Collection<Index> getIndices() {
        return indices.values();
    }

    public String toString() {
        return name;
    }

    public GlobFactory getGlobFactory() {
        return globFactory;
    }

    public <T> void register(Class<T> klass, T t) {
        registered.put(klass, t);
    }

    public <T> T getRegistered(Class<T> klass) {
        return (T) registered.get(klass);
    }

    public <T> T getRegistered(Class<T> klass, T NULL) {
        return (T) registered.getOrDefault(klass, NULL);
    }

    public String describe() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'").append(name).append("' : ");
        for (Field field : keyFields) {
            stringBuilder.append("key : ").append(field.getName()).append(" (").append(field.getDataType()).append(") ");
            printAnnotations(stringBuilder, field);
            stringBuilder.append(", ");
        }
        for (Field field : fieldsByName.values()) {
            if (!field.isKeyField()) {
                stringBuilder.append(field.getName()).append(" (").append(field.getDataType()).append(") ");
                printAnnotations(stringBuilder, field);
                stringBuilder.append(", ");
            }
        }
        if (!fieldsByName.isEmpty()) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    public Comparator<Key> sameKeyComparator() {
        return keyComparator.get();
    }

    private void printAnnotations(StringBuilder stringBuilder, Field field) {
        Collection<Glob> annotations = field.streamAnnotations().collect(Collectors.toList());
        if (!annotations.isEmpty()) {
            stringBuilder.append('[');
            List<String> toStrings = new ArrayList<>();
            for (Glob annotation : annotations) {
                toStrings.add(annotation.getType().getName() + ": " + GlobPrinter.toString(annotation));
            }
            Collections.sort(toStrings);
            for (Iterator<String> iterator = toStrings.iterator(); iterator.hasNext(); ) {
                stringBuilder.append(iterator.next());
                if (iterator.hasNext()) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]");
        }
    }

    synchronized public <T> T get(Property<T> property) {
        int index = property.getIndex();
        if (properties.length < index) {
            properties = Arrays.copyOf(properties, index + 2);
        }
        final Object p = properties[index];
        if (p == null) {
            return (T) (properties[index] = property.build(this));
        }
        return (T) p;
    }

    @Override
    public HashContainer<Key, Glob> getAnnotations() {
        return annotations;
    }
}
