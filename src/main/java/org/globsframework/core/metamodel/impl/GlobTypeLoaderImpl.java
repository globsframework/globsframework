package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.FieldInitializeProcessor;
import org.globsframework.core.metamodel.FieldInitializeProcessorService;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoader;
import org.globsframework.core.metamodel.annotations.*;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.fields.impl.AbstractField;
import org.globsframework.core.metamodel.index.*;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultMultiFieldNotUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultMultiFieldUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultNotUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultUniqueIndex;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.Strings;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.container.specific.HashEmptyGlobContainer;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemAlreadyExists;
import org.globsframework.core.utils.exceptions.MissingInfo;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GlobTypeLoaderImpl implements GlobTypeLoader {
    private DefaultGlobType type;
    private DefaultFieldFactory fieldFactory;
    private final boolean toNiceName;
    private final String[] modelName;
    private final Class<?> targetClass;
    private final String name;
    private final FieldInitializeProcessorService fieldInitializeProcessorService;
    private final Map<Class<?>, Object> registered = new ConcurrentHashMap<>();

    public GlobTypeLoaderImpl(Class<?> targetClass, String[] modelName, String name, boolean toNiceName,
                              FieldInitializeProcessorService fieldInitializeProcessorService) {
        this.modelName = modelName;
        this.toNiceName = toNiceName;
        this.fieldInitializeProcessorService = fieldInitializeProcessorService;
        this.targetClass = targetClass;
        this.name = name;
    }

    public GlobTypeLoader load() {
        checkClassIsNotAlreadyInitialized(targetClass);
        processClass(targetClass);
        processFields(targetClass);
        processIndex(targetClass);
        type.completeInit();
        for (Map.Entry<Class<?>, Object> entry : registered.entrySet()) {
            type.register(((Class) entry.getKey()), entry.getValue());
        }
        processOther(targetClass);
        return this;
    }

    public GlobType getType() {
        return type;
    }

    private void processOther(Class<?> targetClass) {
        for (java.lang.reflect.Field classField : targetClass.getFields()) {
            List<FieldInitializeProcessor<?>> processor = fieldInitializeProcessorService.get(classField);
            if (processor != null && !processor.isEmpty()) {
                HashContainer<Key, Glob> annotations = HashEmptyGlobContainer.Helper.allocate(0);
                annotations = processFieldAnnotations(classField, annotations);
                MutableGlob fieldNameAnnotation = FieldName.create(getFieldName(classField));
                annotations = annotations.put(fieldNameAnnotation.getKey(), fieldNameAnnotation);
                applyProcessor(targetClass, classField, processor, annotations);
            } else {
                try {
                    Object value = classField.get(null);
                    if (value == null) {
                        throw new MissingInfo("Missing initialisation on " + targetClass.getName() + " for " + classField.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void applyProcessor(Class<?> targetClass, java.lang.reflect.Field classField,
                                List<FieldInitializeProcessor<?>> processor,
                                HashContainer<Key, Glob> annotations) {
        DefaultAnnotations defaultAnnotations = new DefaultAnnotations(annotations);
        for (FieldInitializeProcessor<?> fieldInitializeProcessor : processor) {
            Object value = fieldInitializeProcessor.getValue(type, defaultAnnotations, classField.getAnnotations());
            if (value != null) {
                setClassField(classField, value, targetClass);
                return;
            }
        }
    }

    private void checkClassIsNotAlreadyInitialized(Class targetClass) {
        for (java.lang.reflect.Field classField : targetClass.getFields()) {
            try {
                if (classField.getType().equals(GlobType.class)) {
                    if (classField.get(null) != null) {
                        throw new UnexpectedApplicationState(targetClass.getName() + " already initialized");
                    }
                }
            } catch (IllegalAccessException e) {
                throw GlobTypeLoaderImpl.getFieldAccessException(targetClass, classField, null, e);
            }
        }
    }

    private void processClass(Class targetClass) {
        for (java.lang.reflect.Field field : targetClass.getFields()) {
            if (field.getType().equals(GlobType.class) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                createType(field, targetClass);
            }
        }

        if (type == null) {
            throw new MissingInfo("Class " + targetClass.getName() +
                    " must have a TYPE field of class " + GlobType.class.getName());
        }
    }

    private void createType(java.lang.reflect.Field classField,
                            Class<?> targetClass) {
        if (type != null) {
            throw new ItemAlreadyExists("Class " + targetClass.getName() +
                    " must have only one TYPE field of class " + GlobType.class.getName());
        }
        HashContainer<Key, Glob> annotations = HashEmptyGlobContainer.EMPTY_INSTANCE;
        for (Annotation annotation : classField.getAnnotations()) {
            Glob glob = processAnnotation(annotation);
            if (glob != null) {
                annotations = annotations.put(glob.getKey(), glob);
            }
        }
        this.type = new DefaultGlobType(getTypeName(targetClass), annotations);
        this.fieldFactory = new DefaultFieldFactory(type);
        GlobTypeLoaderImpl.setClassField(classField, type, targetClass);
    }

    private HashContainer<Key, Glob> processFieldAnnotations(java.lang.reflect.Field field, HashContainer<Key, Glob> annotations) {
        for (Annotation annotation : field.getAnnotations()) {
            Glob globAnnotation = processAnnotation(annotation);
            if (globAnnotation != null) {
               annotations = annotations.put(globAnnotation.getKey(), globAnnotation);
            }
        }
        return annotations;
    }

    private Glob processAnnotation(Annotation annotation) {
        try {
            if (annotation.annotationType() == Deprecated.class) {
                return null;
            }
            if (annotation.annotationType().isAnnotationPresent(NoType.class)) {
                return null;
            }
            java.lang.reflect.Field[] fields = annotation.getClass().getFields();
            GlobType globType = null;
            java.lang.reflect.Field foundField = null;
            for (java.lang.reflect.Field field : fields) {
                if (field.getType().equals(GlobType.class)) {
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                        globType = (GlobType) field.get(null);
                    } else {
                        foundField = field;
                    }
                }
                if (field.getType().equals(Glob.class)) {
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                        return (Glob) field.get(null);
                    }
                }
            }
            if (globType == null) {
                if (foundField != null) {
                    throw new RuntimeException(foundField.getName() + " must be static public");
                }
                throw new RuntimeException("For " + annotation.annotationType() + " missing GlobType in annotation : code is missing :" +
                        "in annotation : GlobType TYPE = TheAnnotationType.TYPE;");
            } else {
                GlobCreateFromAnnotation registered = globType.getRegistered(GlobCreateFromAnnotation.class);
                if (registered != null) {
                    return registered.create(annotation);
                }
                throw new RuntimeException("For " + annotation.annotationType() + " no GlobCreateFromAnnotation registered. Code like following is missing \n" +
                        "       loader.register(GlobCreateFromAnnotation.class, annotation -> create((theAnnotation)annotation))\n");
            }
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    private void processFields(Class<?> targetClass) {
        int fieldIndex = 0;
        int keyCount = 0;
        Set<Integer> allocatedKeys = new HashSet<>();
        for (java.lang.reflect.Field classField : targetClass.getFields()) {
            int keyIndex = -1;
            if (isGlobField(classField)) {
                boolean isKeyField = classField.isAnnotationPresent(KeyField_.class);
                if (isKeyField) {
                    keyIndex = classField.getAnnotation(KeyField_.class).value();
                    if (keyIndex == -1) {
                        keyIndex = keyCount;
                    }
                    allocatedKeys.add(keyIndex);
                }
                String fieldName;
                boolean hasFieldNameAnnotation = classField.isAnnotationPresent(FieldName_.class);
                if (hasFieldNameAnnotation) {
                    fieldName = classField.getAnnotation(FieldName_.class).value();
                } else {
                    fieldName = getFieldName(classField);
                }
                HashContainer<Key, Glob> annotations = HashEmptyGlobContainer.Helper.allocate(1);
                if (!hasFieldNameAnnotation) {
                    MutableGlob glob = FieldName.create(fieldName);
                    annotations = annotations.put(glob.getKey(), glob);
                }
                if (isKeyField) {
                    Glob glob = KeyField.create(keyCount);
                    annotations = annotations.put(glob.getKey(), glob);
                    keyCount++;
                }
                annotations = processFieldAnnotations(classField, annotations);
                AbstractField field = create(fieldName, classField.getType(), isKeyField, keyIndex, fieldIndex, classField, annotations);
                setClassField(classField, field, targetClass);
                fieldIndex++;
            }
        }
        if (!IntStream.range(0, keyCount).allMatch(allocatedKeys::contains)) {
            throw new RuntimeException("Bug unconstitency between key count " + keyCount + " and key id " + allocatedKeys);
        }
    }

    GlobType getTypeFromClass(Class aClass) {
        for (java.lang.reflect.Field field : aClass.getFields()) {
            if (field.getType().equals(GlobType.class) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                try {
                    GlobType globType = (GlobType) field.get(null);
                    if (globType == null) {
                        throw new RuntimeException("GlobType not initiliazed (missing load on GlobTypeLoader?) on " + aClass.getName());
                    }
                    return globType;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("No static public GlobType field found in " + aClass.getName());
    }

    private AbstractField create(String name, Class<?> fieldClass, boolean isKeyField, int keyIndex, int index,
                                 java.lang.reflect.Field field, HashContainer<Key, Glob> annotations) {
        if (StringField.class.isAssignableFrom(fieldClass)) {
            DefaultString_ defaultString = field.getAnnotation(DefaultString_.class);
            String defaultValue = defaultString != null ? defaultString.value() : null;
            return fieldFactory.addString(name, isKeyField, keyIndex, index, defaultValue, annotations);
        } else if (IntegerField.class.isAssignableFrom(fieldClass)) {
            DefaultInteger_ defaultInteger = field.getAnnotation(DefaultInteger_.class);
            return fieldFactory.addInteger(name, isKeyField, keyIndex, index,
                    defaultInteger != null ? defaultInteger.value() : null, annotations);
        } else if (LongField.class.isAssignableFrom(fieldClass)) {
            DefaultLong_ defaultLong = field.getAnnotation(DefaultLong_.class);
            return fieldFactory.addLong(name, isKeyField, keyIndex, index,
                    defaultLong != null ? defaultLong.value() : null, annotations);
        } else if (BooleanField.class.isAssignableFrom(fieldClass)) {
            DefaultBoolean_ defaultBoolean = field.getAnnotation(DefaultBoolean_.class);
            return fieldFactory.addBoolean(name, isKeyField, keyIndex, index,
                    defaultBoolean != null ? defaultBoolean.value() : null, annotations);
        } else if (DoubleField.class.isAssignableFrom(fieldClass)) {
            DefaultDouble_ defaultDouble = field.getAnnotation(DefaultDouble_.class);
            return fieldFactory.addDouble(name, isKeyField, keyIndex, index,
                    defaultDouble != null ? defaultDouble.value() : null, annotations);
        } else if (BytesField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addBytes(name, index, annotations);
        } else if (DoubleArrayField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addDoubleArray(name, isKeyField, keyIndex, index, annotations);
        } else if (IntegerArrayField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addIntegerArray(name, isKeyField, keyIndex, index, annotations);
        } else if (BooleanArrayField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addBooleanArray(name, isKeyField, keyIndex, index, annotations);
        } else if (LongArrayField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addLongArray(name, isKeyField, keyIndex, index, annotations);
        } else if (StringArrayField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addStringArray(name, isKeyField, keyIndex, index, annotations);
        } else if (DateField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addDate(name, isKeyField, keyIndex, index, annotations);
        } else if (DateTimeField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addDateTime(name, isKeyField, keyIndex, index, annotations);
        } else if (BigDecimalField.class.isAssignableFrom(fieldClass)) {
            DefaultBigDecimal_ defaultBigDecimal = field.getAnnotation(DefaultBigDecimal_.class);
            return fieldFactory.addBigDecimal(name, isKeyField, keyIndex, index,
                    defaultBigDecimal != null ? new BigDecimal(defaultBigDecimal.value()) : null,
                    annotations);
        } else if (BigDecimalArrayField.class.isAssignableFrom(fieldClass)) {
            return fieldFactory.addBigDecimalArray(name, isKeyField, keyIndex, index, annotations);
        } else if (GlobField.class.isAssignableFrom(fieldClass)) {
            Target annotation = field.getAnnotation(Target.class);
            if (annotation == null) {
                throw new RuntimeException("Missing Target annotation on " + this.name + "." + name);
            }
            GlobType type;
            try {
                type = (GlobType) annotation.value().getField("TYPE").get(null);
            } catch (Exception e) {
                throw new RuntimeException("Can not find a static GlobType named TYPE in " + annotation.value().getName(), e);
            }
            return fieldFactory.addGlob(name, type, isKeyField, keyIndex, index, annotations);
        } else if (GlobArrayField.class.isAssignableFrom(fieldClass)) {
            Target annotation = field.getAnnotation(Target.class);
            if (annotation == null) {
                throw new RuntimeException("Missing Target annotation on " + this.name + "." + name);
            }
            GlobType type = getTypeFromClass(annotation.value());
            return fieldFactory.addGlobArray(name, type, isKeyField, keyIndex, index, annotations);
        } else if (GlobUnionField.class.isAssignableFrom(fieldClass)) {
            Targets annotation = field.getAnnotation(Targets.class);
            if (annotation == null) {
                throw new RuntimeException("Missing Targets annotation on " + this.name + "." + name);
            }
            List<GlobType> types = Arrays.stream(annotation.value()).map(this::getTypeFromClass).collect(Collectors.toList());
            return fieldFactory.addGlobUnion(name, types, index, annotations);
        } else if (GlobArrayUnionField.class.isAssignableFrom(fieldClass)) {
            Targets annotation = field.getAnnotation(Targets.class);
            if (annotation == null) {
                throw new RuntimeException("Missing Targets annotation on " + this.name + "." + name);
            }
            List<GlobType> types = Arrays.stream(annotation.value()).map(this::getTypeFromClass).collect(Collectors.toList());
            return fieldFactory.addGlobArrayUnion(name, types, index, annotations);
        } else {
            throw new InvalidParameter("Unknown type " + fieldClass.getName());
        }
    }

    private void processIndex(Class<?> targetClass) {
        for (java.lang.reflect.Field classField : targetClass.getFields()) {
            if (isUniqueIndexField(classField)) {
                SingleFieldIndex index = fieldFactory.addUniqueIndex(classField.getName());
                setClassField(classField, index, targetClass);
                type.addIndex(index);
            }
            if (isNotUniqueIndexField(classField)) {
                SingleFieldIndex index = fieldFactory.addNotUniqueIndex(classField.getName());
                setClassField(classField, index, targetClass);
                type.addIndex(index);
            }
            if (isMultiFieldNotUniqueIndexField(classField)) {
                MultiFieldNotUniqueIndex index = fieldFactory.addMultiFieldNotUniqueIndex(classField.getName());
                setClassField(classField, index, targetClass);
                type.addIndex(index);
            }
            if (isMultiFieldUniqueIndexField(classField)) {
                MultiFieldUniqueIndex index = fieldFactory.addMultiFieldUniqueIndex(classField.getName());
                setClassField(classField, index, targetClass);
                type.addIndex(index);
            }
        }
    }

    private boolean isMultiFieldUniqueIndexField(java.lang.reflect.Field field) {
        return MultiFieldUniqueIndex.class.isAssignableFrom(field.getType());
    }

    private boolean isMultiFieldNotUniqueIndexField(java.lang.reflect.Field field) {
        return MultiFieldNotUniqueIndex.class.isAssignableFrom(field.getType());
    }

    private boolean isNotUniqueIndexField(java.lang.reflect.Field field) {
        return NotUniqueIndex.class.isAssignableFrom(field.getType());
    }

    private boolean isUniqueIndexField(java.lang.reflect.Field field) {
        return UniqueIndex.class.isAssignableFrom(field.getType());
    }

    private static void setClassField(java.lang.reflect.Field classField, Object value, Class<?> targetClass) {
        try {
            classField.set(null, value);
        } catch (Exception e) {
            throw GlobTypeLoaderImpl.getFieldAccessException(targetClass, classField, value, e);
        }
    }

    private static RuntimeException getFieldAccessException(Class<?> targetClass, java.lang.reflect.Field classField, Object value, Exception e) {
        String valueDescription;
        if (value != null) {
            valueDescription = value.toString() + " (class " + value.getClass().getName() + ")";
        } else {
            valueDescription = "'null'";
        }
        return new RuntimeException("Unable to initialize field " + targetClass.getName() + "." + classField.getName() +
                " with value " + valueDescription, e);
    }

    GlobTypeLoader addField(Field field) throws ItemAlreadyExists {
        if (type.hasField(field.getName())) {
            throw new ItemAlreadyExists("Field " + field.getName() +
                    " declared twice for type " + type.getName());
        }
        type.addField(field);
        return this;
    }

    private String getTypeName(Class<?> aClass) {
        if (name != null) {
            return name;
        } else {
            String fullName = aClass.getName();
            int lastSeparatorIndex = Math.max(fullName.lastIndexOf("."), fullName.lastIndexOf("$"));
            String extractedName = fullName.substring(lastSeparatorIndex + 1);
            return toNiceName ? Strings.uncapitalize(extractedName) : extractedName;
        }
    }

    private String getFieldName(java.lang.reflect.Field field) {
        if (field.getName().length() == 1 || !toNiceName) {
            return field.getName();
        }
        for (char c : field.getName().toCharArray()) {
            if (Character.isLowerCase(c)) {
                return field.getName();
            }
        }
        return Strings.toNiceLowerCase(field.getName());
    }

    private boolean isGlobField(java.lang.reflect.Field field) {
        return Field.class.isAssignableFrom(field.getType());
    }

    private boolean isGlobLink(java.lang.reflect.Field field) {
        return Link.class.isAssignableFrom(field.getType());
    }

    public GlobTypeLoader defineUniqueIndex(UniqueIndex index, Field field) {
        if (index == null) {
            throw new RuntimeException("index is null. Was load call before?");
        }
        ((DefaultUniqueIndex) type.getIndex(index.getName())).setField(field);
        return this;
    }

    public GlobTypeLoader defineNonUniqueIndex(NotUniqueIndex index, Field field) {
        if (index == null) {
            throw new RuntimeException("index is null. Was load call before?");
        }
        ((DefaultNotUniqueIndex) type.getIndex(index.getName())).setField(field);
        return this;
    }

    public GlobTypeLoader defineMultiFieldUniqueIndex(MultiFieldUniqueIndex index, Field... fields) {
        if (index == null) {
            throw new RuntimeException("index is null. Was load call before?");
        }
        ((DefaultMultiFieldUniqueIndex) type.getIndex(index.getName())).setField(fields);
        return this;
    }

    public GlobTypeLoader defineMultiFieldNotUniqueIndex(MultiFieldNotUniqueIndex index, Field... fields) {
        if (index == null) {
            throw new RuntimeException("index is null. Was load call before?");
        }
        ((DefaultMultiFieldNotUniqueIndex) type.getIndex(index.getName())).setField(fields);
        return this;
    }

    public <T> GlobTypeLoader register(Class<T> klass, T t) {
        registered.put(klass, t);
        return this;
    }

    public <T> GlobTypeLoader register(Field field, Class<T> klass, T t) {
        ((AbstractField) field).register(klass, t);
        return this;
    }
}
