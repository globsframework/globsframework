package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.fields.impl.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.container.specific.HashEmptyGlobContainer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class DefaultGlobTypeBuilder implements GlobTypeBuilder {
    private DefaultGlobType type;
    private DefaultFieldFactory factory;
    private int index;
    private int keyIndex;

    public DefaultGlobTypeBuilder(String name, Collection<Glob> annotations) {
        type = new DefaultGlobType(name, adaptAnnotation(annotations));
        factory = new DefaultFieldFactory(type);
    }

    public static GlobTypeBuilder init(String typeName) {
        return new DefaultGlobTypeBuilder(typeName);
    }

    public DefaultGlobTypeBuilder(String typeName) {
        type = new DefaultGlobType(typeName);
        factory = new DefaultFieldFactory(type);
    }

    public static GlobTypeBuilder init(String name, Collection<Glob> annotations) {
        return new DefaultGlobTypeBuilder(name, annotations);
    }

    private HashContainer<Key, Glob> adaptAnnotation(Collection<Glob> annotations) {
        HashContainer<Key, Glob> container = HashEmptyGlobContainer.Helper.allocate(annotations.size());
        for (Glob annotation : annotations) {
            if (annotation != null) {
                container = container.put(annotation.getKey(), annotation);
            }
        }
        return container;
    }

    public GlobTypeBuilder addAnnotation(Glob annotation) {
        type.addAnnotation(annotation);
        return this;
    }

    public GlobTypeBuilder addAnnotations(Collection<Glob> annotations) {
        type.addAnnotations(annotations);
        return this;
    }

    public GlobTypeBuilder addStringField(String fieldName, Collection<Glob> annotations) {
        createStringField(fieldName, annotations);
        return this;
    }

    private DefaultStringField createStringField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob defaultValue = annotations.get(DefaultString.KEY);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultStringField field = factory.addString(fieldName, keyPos != -1, keyPos, index,
                defaultValue != null ? defaultValue.get(DefaultString.VALUE) : null, annotations);
        index++;
        return field;
    }

    public GlobTypeBuilder addStringArrayField(String fieldName, Collection<Glob> annotations) {
        createStringArrayField(fieldName, annotations);
        return this;
    }

    private DefaultStringArrayField createStringArrayField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultStringArrayField field = factory.addStringArray(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return field;
    }

    public GlobTypeBuilder addIntegerField(String fieldName, Collection<Glob> globAnnotations) {
        createIntegerField(fieldName, globAnnotations);
        return this;
    }

    private DefaultIntegerField createIntegerField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob defaultValue = annotations.get(DefaultInteger.KEY);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultIntegerField field = factory.addInteger(fieldName, keyPos != -1, keyPos, index,
                defaultValue != null ? defaultValue.get(DefaultInteger.VALUE) : null, annotations);
        index++;
        return field;
    }

    private int getOrUpdateKeyPos(HashContainer<Key, Glob> annotations) {
        Glob key = annotations.get(KeyField.UNIQUE_KEY);
        int keyPos = -1;
        if (key != null) {
            if ((keyPos = key.get(KeyField.INDEX, -1)) == -1) {
                keyPos = keyIndex++;
                Glob glob = KeyField.create(keyPos);
                annotations.put(glob.getKey(), glob); // replace, the HashContainer is the same.
            }
        }
        return keyPos;
    }

    private DefaultIntegerArrayField createIntegerArrayField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultIntegerArrayField field = factory.addIntegerArray(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return field;
    }

    public GlobTypeBuilder addDoubleField(String fieldName, Collection<Glob> globAnnotations) {
        createDoubleField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addDoubleArrayField(String fieldName, Collection<Glob> globAnnotations) {
        createDoubleArrayField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addIntegerArrayField(String fieldName, Collection<Glob> globAnnotations) {
        createIntegerArrayField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addLongArrayField(String fieldName, Collection<Glob> globAnnotations) {
        createLongArrayField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addBigDecimalField(String fieldName, Collection<Glob> globAnnotations) {
        createBigDecimalField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addBigDecimalArrayField(String fieldName, Collection<Glob> globAnnotations) {
        createBigDecimalArrayField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addDateField(String fieldName, Collection<Glob> globAnnotations) {
        createDateField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addDateTimeField(String fieldName, Collection<Glob> globAnnotations) {
        createDateTimeField(fieldName, globAnnotations);
        return this;
    }

    private DefaultDoubleField createDoubleField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob defaultValue = annotations.get(DefaultDouble.KEY);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultDoubleField doubleField = factory.addDouble(fieldName, keyPos != -1, keyPos, index,
                defaultValue != null ? defaultValue.get(DefaultDouble.VALUE) : null, annotations);
        index++;
        return doubleField;
    }

    private DefaultDoubleArrayField createDoubleArrayField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultDoubleArrayField field = factory.addDoubleArray(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return field;
    }

    private DefaultBigDecimalField createBigDecimalField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob defaultValue = annotations.get(DefaultBigDecimal.KEY);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultBigDecimalField bigDecimalField = factory.addBigDecimal(fieldName, keyPos != -1, keyPos, index,
                defaultValue != null ? defaultValue.get(DefaultBigDecimal.VALUE) : null, annotations);
        index++;
        return bigDecimalField;
    }

    private DefaultBigDecimalArrayField createBigDecimalArrayField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultBigDecimalArrayField bigDecimalArrayField =
                factory.addBigDecimalArray(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return bigDecimalArrayField;
    }

    private DefaultDateTimeField createDateTimeField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultDateTimeField dateTimeField = factory.addDateTime(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return dateTimeField;
    }

    private DefaultDateField createDateField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultDateField dateField = factory.addDate(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return dateField;
    }


    public GlobTypeBuilder addLongField(String fieldName, Collection<Glob> globAnnotations) {
        createLongField(fieldName, globAnnotations);
        return this;
    }

    private DefaultLongField createLongField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob defaultValue = annotations.get(DefaultLong.KEY);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultLongField longField = factory.addLong(fieldName, keyPos != -1, keyPos, index,
                defaultValue != null ? defaultValue.get(DefaultLong.VALUE) : null, annotations);
        index++;
        return longField;
    }

    private DefaultLongArrayField createLongArrayField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultLongArrayField field = factory.addLongArray(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return field;
    }

    public GlobTypeBuilder addBooleanArrayField(String fieldName, Collection<Glob> globAnnotations) {
        createBooleanArrayField(fieldName, globAnnotations);
        return this;
    }

    private DefaultBooleanArrayField createBooleanArrayField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultBooleanArrayField field = factory.addBooleanArray(fieldName, keyPos != -1, keyPos, index, annotations);
        index++;
        return field;
    }

    public GlobTypeBuilder addBooleanField(String fieldName, Collection<Glob> globAnnotations) {
        createBooleanField(fieldName, globAnnotations);
        return this;
    }

    private DefaultBooleanField createBooleanField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob defaultValue = annotations.get(DefaultBoolean.KEY);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultBooleanField field = factory.addBoolean(fieldName, keyPos != -1, keyPos, index,
                defaultValue != null ? defaultValue.get(DefaultBoolean.VALUE) : null, annotations);
        index++;
        return field;
    }

    public GlobTypeBuilder addBytesField(String fieldName, Collection<Glob> globAnnotations) {
        createBytesField(fieldName, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addGlobField(String fieldName, Collection<Glob> globAnnotations, GlobType type) {
        createGlobField(fieldName, type, globAnnotations);
        return this;
    }


    public GlobTypeBuilder addGlobArrayField(String fieldName, Collection<Glob> globAnnotations, GlobType type) {
        createGlobArrayField(fieldName, type, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addUnionGlobField(String fieldName, Collection<Glob> globAnnotations, List<GlobType> types) {
        createGlobUnionField(fieldName, types, globAnnotations);
        return this;
    }

    public GlobTypeBuilder addUnionGlobArrayField(String fieldName, Collection<Glob> globAnnotations, List<GlobType> types) {
        createGlobUnionArrayField(fieldName, types, globAnnotations);
        return this;
    }

    private DefaultBytesField createBytesField(String fieldName, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        DefaultBytesField field = factory.addBytes(fieldName, index, annotations);
        index++;
        return field;
    }

    private GlobField createGlobField(String fieldName, GlobType globType, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultGlobField field = factory.addGlob(fieldName, globType, keyPos != -1, keyPos, index, annotations);
        index++;
        return field;
    }

    private GlobArrayField createGlobArrayField(String fieldName, GlobType globType, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        int keyPos = getOrUpdateKeyPos(annotations);
        DefaultGlobArrayField field = factory.addGlobArray(fieldName, globType, keyPos != -1,
                keyPos, index, annotations);
        index++;
        return field;
    }

    private GlobUnionField createGlobUnionField(String fieldName, Collection<GlobType> types, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob key = annotations.get(KeyField.UNIQUE_KEY);
        if (key != null) {
            throw new RuntimeException(fieldName + " of type unionField cannot be a key");
        }
        DefaultGlobUnionField field = factory.addGlobUnion(fieldName, types, index, annotations);
        index++;
        return field;
    }

    private GlobArrayUnionField createGlobUnionArrayField(String fieldName, Collection<GlobType> types, Collection<Glob> globAnnotations) {
        HashContainer<Key, Glob> annotations = adaptAnnotation(globAnnotations);
        Glob key = annotations.get(KeyField.UNIQUE_KEY);
        if (key != null) {
            throw new RuntimeException(fieldName + " of type unionField cannot be a key");
        }
        DefaultGlobUnionArrayField field = factory.addGlobArrayUnion(fieldName, types, index, annotations);
        index++;
        return field;
    }

    public StringField declareStringField(String fieldName, Collection<Glob> globAnnotations) {
        return createStringField(fieldName, globAnnotations);
    }

    public StringArrayField declareStringArrayField(String fieldName, Collection<Glob> globAnnotations) {
        return createStringArrayField(fieldName, globAnnotations);
    }

    public IntegerField declareIntegerField(String fieldName, Collection<Glob> annotations) {
        return createIntegerField(fieldName, annotations);
    }

    public IntegerArrayField declareIntegerArrayField(String fieldName, Collection<Glob> annotations) {
        return createIntegerArrayField(fieldName, annotations);
    }

    public DoubleField declareDoubleField(String fieldName, Collection<Glob> annotations) {
        return createDoubleField(fieldName, annotations);
    }

    public DoubleArrayField declareDoubleArrayField(String fieldName, Collection<Glob> annotations) {
        return createDoubleArrayField(fieldName, annotations);
    }

    public BigDecimalField declareBigDecimalField(String fieldName, Collection<Glob> annotations) {
        return createBigDecimalField(fieldName, annotations);
    }

    public BigDecimalArrayField declareBigDecimalArrayField(String fieldName, Collection<Glob> annotations) {
        return createBigDecimalArrayField(fieldName, annotations);
    }

    public BooleanField declareBooleanField(String fieldName, Collection<Glob> annotations) {
        return createBooleanField(fieldName, annotations);
    }

    public BooleanArrayField declareBooleanArrayField(String fieldName, Collection<Glob> annotations) {
        return createBooleanArrayField(fieldName, annotations);
    }

    public DateField declareDateField(String fieldName, Collection<Glob> annotations) {
        return createDateField(fieldName, annotations);
    }

    public DateTimeField declareDateTimeField(String fieldName, Collection<Glob> annotations) {
        return createDateTimeField(fieldName, annotations);
    }

    public LongField declareLongField(String fieldName, Collection<Glob> annotations) {
        return createLongField(fieldName, annotations);
    }

    public LongArrayField declareLongArrayField(String fieldName, Collection<Glob> annotations) {
        return createLongArrayField(fieldName, annotations);
    }

    public BytesField declareBytesField(String fieldName, Collection<Glob> annotations) {
        return createBytesField(fieldName, annotations);
    }

    public GlobField declareGlobField(String fieldName, GlobType globType, Collection<Glob> annotations) {
        return createGlobField(fieldName, globType, annotations);
    }

    public GlobArrayField declareGlobArrayField(String fieldName, GlobType globType, Collection<Glob> annotations) {
        return createGlobArrayField(fieldName, globType, annotations);
    }

    public GlobUnionField declareGlobUnionField(String fieldName, Collection<GlobType> types, Collection<Glob> annotations) {
        return createGlobUnionField(fieldName, types, annotations);
    }

    public GlobArrayUnionField declareGlobUnionArrayField(String fieldName, Collection<GlobType> types, Collection<Glob> annotations) {
        return createGlobUnionArrayField(fieldName, types, annotations);
    }

    public Field declare(String fieldName, DataType dataType, Collection<Glob> annotations) {
        if (fieldName == null) {
            throw new RuntimeException("field name can not be null");
        }
        switch (dataType) {
            case String:
                return declareStringField(fieldName, annotations);
            case StringArray:
                return declareStringArrayField(fieldName, annotations);
            case Double:
                return declareDoubleField(fieldName, annotations);
            case DoubleArray:
                return declareDoubleArrayField(fieldName, annotations);
            case BigDecimal:
                return declareBigDecimalField(fieldName, annotations);
            case BigDecimalArray:
                return declareBigDecimalArrayField(fieldName, annotations);
            case Long:
                return declareLongField(fieldName, annotations);
            case LongArray:
                return declareLongArrayField(fieldName, annotations);
            case Integer:
                return declareIntegerField(fieldName, annotations);
            case IntegerArray:
                return declareIntegerArrayField(fieldName, annotations);
            case Boolean:
                return declareBooleanField(fieldName, annotations);
            case BooleanArray:
                return declareBooleanArrayField(fieldName, annotations);
            case Date:
                return declareDateField(fieldName, annotations);
            case DateTime:
                return declareDateTimeField(fieldName, annotations);
            case Bytes:
                return declareBytesField(fieldName, annotations);
        }
        throw new RuntimeException("creation of " + dataType + " not possible without additional parameter (globType)");
    }

    /*
    TODO : remove annotations FieldName, Index, DefaultValue from annotations : it is a duplication.
    Or replace them here with the
     */

    public Field declareFrom(String name, Field field) {
        boolean isKeyField = field.isKeyField();
        final HashContainer<Key, Glob> hashContainer = ((DefaultAnnotations) field).getInternal();
//        HashContainer<Key, Glob> duplicate = hashContainer.duplicate();
//        MutableGlob glob = FieldName.create(name);
//        duplicate.put(glob.getKey(), glob);

        Field newField = switch (field.getDataType()) {
            case String -> factory.addString(name, field.isKeyField(), keyIndex, index,
                    ((String) field.getDefaultValue()), hashContainer);
            case StringArray -> factory.addStringArray(name, field.isKeyField(), keyIndex, index, hashContainer);
            case Double ->
                    factory.addDouble(name, field.isKeyField(), keyIndex, index, ((Double) field.getDefaultValue()),
                            hashContainer);
            case DoubleArray -> factory.addDoubleArray(name, field.isKeyField(), keyIndex, index, hashContainer);
            case BigDecimal ->
                    factory.addBigDecimal(name, field.isKeyField(), keyIndex, index, ((BigDecimal) field.getDefaultValue()),
                            hashContainer);
            case BigDecimalArray ->
                    factory.addBigDecimalArray(name, field.isKeyField(), keyIndex, index, hashContainer);
            case Long -> factory.addLong(name, field.isKeyField(), keyIndex, index, ((Long) field.getDefaultValue()),
                    hashContainer);
            case LongArray -> factory.addLongArray(name, field.isKeyField(), keyIndex, index, hashContainer);
            case Integer ->
                    factory.addInteger(name, field.isKeyField(), keyIndex, index, ((Integer) field.getDefaultValue()),
                            hashContainer);
            case IntegerArray -> factory.addIntegerArray(name, field.isKeyField(), keyIndex, index, hashContainer);
            case Boolean ->
                    factory.addBoolean(name, field.isKeyField(), keyIndex, index, ((Boolean) field.getDefaultValue()),
                            hashContainer);
            case BooleanArray -> factory.addBooleanArray(name, field.isKeyField(), keyIndex, index, hashContainer);
            case Date -> factory.addDate(name, field.isKeyField(), keyIndex, index, hashContainer);
            case DateTime -> factory.addDateTime(name, field.isKeyField(), keyIndex, index, hashContainer);
            case Bytes -> factory.addBytes(name, index, hashContainer);
            case Glob ->
                    factory.addGlob(name, ((GlobField) field).getTargetType(), isKeyField, keyIndex, index, hashContainer);
            case GlobArray ->
                    factory.addGlobArray(name, ((GlobArrayField) field).getTargetType(), isKeyField, keyIndex, index, hashContainer);
            case GlobUnion ->
                    factory.addGlobUnion(name, ((GlobUnionField) field).getTargetTypes(), index, hashContainer);
            case GlobUnionArray ->
                    factory.addGlobArrayUnion(name, ((GlobUnionField) field).getTargetTypes(), index, hashContainer);
        };
        index++;
        if (isKeyField) {
            keyIndex++;
        }
        return newField;
    }

    public <T> GlobTypeBuilder register(Class<T> klass, T t) {
        type.register(klass, t);
        return this;
    }

    public <T> GlobTypeBuilder register(Field field, Class<T> klass, T t) {
        ((AbstractField) field).register(klass, t);
        return this;
    }

    public GlobType get() {
        type.completeInit();
        return type;
    }

    public void complete() {
        type.completeInit();
    }

    public GlobType unCompleteType() {
        return type;
    }

    public boolean isKnown(String fieldName) {
        return type.hasField(fieldName);
    }
}
