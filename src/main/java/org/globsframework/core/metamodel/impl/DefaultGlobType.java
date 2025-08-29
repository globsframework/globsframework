package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.index.Index;
import org.globsframework.core.metamodel.index.MultiFieldIndex;
import org.globsframework.core.metamodel.index.SingleFieldIndex;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.metamodel.utils.MutableGlobType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobFactory;
import org.globsframework.core.model.GlobFactoryService;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.InvalidState;
import org.globsframework.core.utils.exceptions.ItemAlreadyExists;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.TooManyItems;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DefaultGlobType extends DefaultAnnotations
        implements MutableGlobType, MutableAnnotations {
    public static final String[] EMPTY_SCOPE = new String[0];
    private Field[] fields;
    private Field[] keyFields = new Field[0];
    private GlobFactory globFactory;
    private Comparator<Key> keyComparator;
    private final String name;
    private final Map<String, Field> fieldsByName = new HashMap<>();
    private final Map<String, Index> indices = new HashMap<>(2, 1);
    private final Map<Class<?>, Object> registered = new ConcurrentHashMap<>();
    private Object[] properties = new Object[2];

    public DefaultGlobType(String name) {
        this(name, null);
    }

    public DefaultGlobType(String name, HashContainer<Key, Glob> annotations) {
        super(annotations);
        this.name = name;
    }

    public int getFieldCount() {
        return fieldsByName.size();
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


    public void addField(Field field) {
        if (hasField(field.getName())) {
            throw new ItemAlreadyExists("Field " + field.getName() +
                    " declared twice for type " + getName());
        }
        if (field.getIndex() != fieldsByName.size()) {
            throw new InvalidState(field + " should be at index " + field.getIndex() + " but is at" + fieldsByName.size());
        }
        fieldsByName.put(field.getName(), field);
    }

    public void addKey(Field field) {
        Field[] tmp = new Field[Math.max(field.getKeyIndex() + 1, keyFields.length)];
        System.arraycopy(keyFields, 0, tmp, 0, keyFields.length);
        keyFields = tmp;
        keyFields[field.getKeyIndex()] = field;
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

    public String toString() {
        return name;
    }

    public void completeInit() {
        if (fields != null) {
            if (fields.length != fieldsByName.size()) {
                throw new InvalidState("Update after complete : field count should be " + fieldsByName.size() + " but is " + fields.length + " for " + this);
            }
            return;
        }
        fields = new Field[fieldsByName.size()];
        for (Field field : fieldsByName.values()) {
            fields[field.getIndex()] = field;
        }
        if (FieldCheck.CheckGlob.shouldCheck) {
            int keyFieldCount = 0;
            Set<Integer> keySet = new HashSet<>();
            for (Field field : fields) {
                Glob annotation = field.findAnnotation(KeyField.UNIQUE_KEY);
                if (annotation != null) {
                    int index = annotation.get(KeyField.INDEX, -1);
                    if (index == -1) {
                        throw new RuntimeException("key annotation is not initialized");
                    } else {
                        keySet.add(index);
                    }
                    keyFieldCount++;
                }
            }
            if (!IntStream.range(0, keyFieldCount).allMatch(keySet::contains)) {
                throw new RuntimeException("Bug un-consistency between key count " + keyFieldCount + " and key id " + keySet + " (must start at 0)");
            }
        }

        globFactory = GlobFactoryService.Builder.getBuilderFactory().getFactory(this);

        Comparator<Key> cmp = null;
        for (Field keyField : keyFields) {
            if (cmp == null) {
                cmp = Comparator.comparing(key -> (Comparable) key.getValue(keyField));
            } else {
                cmp = cmp.thenComparing(key -> (Comparable) key.getValue(keyField));
            }
        }
        this.keyComparator = cmp;
    }

    void addIndex(SingleFieldIndex index) {
        indices.put(index.getName(), index);
    }

    void addIndex(MultiFieldIndex index) {
        indices.put(index.getName(), index);
    }

    public Collection<Index> getIndices() {
        return indices.values();
    }

    public GlobFactory getGlobFactory() {
        return globFactory;
    }

    public Index getIndex(String name) {
        return indices.get(name);
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
        return keyComparator;
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
            return (T) (properties[index] = property.build( this ));
        }
        return (T) p;
    }
}
