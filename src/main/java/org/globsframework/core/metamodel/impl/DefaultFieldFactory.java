package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.impl.*;
import org.globsframework.core.metamodel.index.MultiFieldNotUniqueIndex;
import org.globsframework.core.metamodel.index.MultiFieldUniqueIndex;
import org.globsframework.core.metamodel.index.NotUniqueIndex;
import org.globsframework.core.metamodel.index.UniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultMultiFieldNotUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultMultiFieldUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultNotUniqueIndex;
import org.globsframework.core.metamodel.index.impl.DefaultUniqueIndex;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.ItemAlreadyExists;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultFieldFactory {
    private final Supplier<GlobType> type;
    private final Map<String, Field> fields;

    public DefaultFieldFactory(Supplier<GlobType> type, Map<String, Field> fields) {
        this.type = type;
        this.fields = fields;
    }

    public DefaultIntegerField addInteger(String name,
                                          boolean isKeyField,
                                          int keyIndex, int index,
                                          Integer defaultValue, HashContainer<Key, Glob> annotations) {
        return add(new DefaultIntegerField(name, type, index, isKeyField, keyIndex, defaultValue, annotations), isKeyField);
    }

    public DefaultIntegerArrayField addIntegerArray(String name,
                                                    boolean isKeyField,
                                                    int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultIntegerArrayField(name, type, index, isKeyField, keyIndex, null, annotations), false);
    }

    public DefaultBooleanArrayField addBooleanArray(String name,
                                                    boolean isKeyField,
                                                    int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultBooleanArrayField(name, type, index, isKeyField, keyIndex, null, annotations), false);
    }

    public DefaultLongField addLong(String name,
                                    boolean isKeyField,
                                    int keyIndex, int index,
                                    Long defaultValue, HashContainer<Key, Glob> annotations) {
        return add(new DefaultLongField(name, type, index, isKeyField, keyIndex, defaultValue, annotations), isKeyField);
    }

    public DefaultLongArrayField addLongArray(String name,
                                              boolean isKeyField,
                                              int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultLongArrayField(name, type, index, isKeyField, keyIndex, null, annotations), false);
    }

    public DefaultDoubleField addDouble(String name,
                                        boolean isKeyField,
                                        final int keyIndex, int index,
                                        Double defaultValue, HashContainer<Key, Glob> annotations) {
        return add(new DefaultDoubleField(name, type, index, isKeyField, keyIndex, defaultValue, annotations), isKeyField);
    }

    public DefaultDoubleArrayField addDoubleArray(String name,
                                                  boolean isKeyField,
                                                  int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultDoubleArrayField(name, type, index, isKeyField, keyIndex, null, annotations), false);
    }

    public DefaultStringField addString(String name,
                                        boolean isKeyField,
                                        int keyIndex, int index,
                                        String defaultValue, HashContainer<Key, Glob> annotations) {
        return add(new DefaultStringField(name, type, index, isKeyField, keyIndex, defaultValue, annotations), isKeyField);
    }

    public DefaultStringArrayField addStringArray(String name,
                                                  boolean isKeyField,
                                                  int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultStringArrayField(name, type, index, isKeyField, keyIndex, null, annotations), false);
    }

    public DefaultBooleanField addBoolean(String name,
                                          boolean isKeyField,
                                          final int keyIndex, int index,
                                          Boolean defaultValue, HashContainer<Key, Glob> annotations) {
        return add(new DefaultBooleanField(name, type, index, isKeyField, keyIndex, defaultValue, annotations), isKeyField);
    }

    public DefaultBigDecimalField addBigDecimal(String name,
                                                boolean isKeyField,
                                                int keyIndex, int index,
                                                BigDecimal defaultValue, HashContainer<Key, Glob> annotations) {
        return add(new DefaultBigDecimalField(name, type, index, isKeyField, keyIndex, defaultValue, annotations), false);
    }

    public DefaultBigDecimalArrayField addBigDecimalArray(String name,
                                                          boolean isKeyField,
                                                          int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultBigDecimalArrayField(name, type, index, isKeyField, keyIndex, null, annotations), false);
    }

    public DefaultDateTimeField addDateTime(String name,
                                            boolean isKeyField,
                                            int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultDateTimeField(name, type, index, isKeyField, keyIndex, null, annotations), isKeyField);
    }

    public DefaultDateField addDate(String name,
                                    boolean isKeyField,
                                    int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultDateField(name, type, index, isKeyField, keyIndex, null, annotations), isKeyField);
    }

    public DefaultBytesField addBytes(String name, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultBytesField(name, type, index, annotations), false);
    }

    public DefaultGlobField addGlob(String name, Supplier<GlobType> globType, boolean isKeyField,
                                    int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultGlobField(name, type, globType, index, isKeyField, keyIndex, annotations), isKeyField);
    }

    public DefaultGlobArrayField addGlobArray(String name, Supplier<GlobType> globType, boolean isKeyField,
                                              int keyIndex, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultGlobArrayField(name, type, globType, index, isKeyField, keyIndex, annotations), isKeyField);
    }

    public DefaultGlobUnionField addGlobUnion(String fieldName, Supplier<GlobType>[] types,
                                              int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultGlobUnionField(fieldName, type, types, index, false, 0, annotations), false);
    }

    public DefaultGlobUnionArrayField addGlobArrayUnion(String fieldName, Supplier<GlobType>[] types, int index, HashContainer<Key, Glob> annotations) {
        return add(new DefaultGlobUnionArrayField(fieldName, type, types, index, false, 0, annotations), false);
    }

    private <T extends Field> T add(T field, boolean isKeyField) {
        if (fields.put(field.getName(), field) != null) {
            throw new ItemAlreadyExists("Duplicate field '" + field.getName() + "' in type '" + type.get().getName() + "'");
        }
        return field;
    }

    //    private static class DefaultLinkField extends AbstractField implements LinkField {
//        private GlobType targetType;
//        private IntegerField targetKeyField;
//
//        private DefaultLinkField(String name, GlobType globType,
//                                 Annotations annotations) {
//            super(name, globType, Integer.class, annotations);
//
//            Glob annotation = annotations.findAnnotation(TargetAnnotationType.KEY);
//            if (annotation == null) {
//                throw new MissingInfo("Annotation " + Target.class.getName() +
//                        " must be specified for LinkField '" + name +
//                        "' for type: " + globType.getName());
//            }
//            Class targetClass = ((Target) annotations.get(Target.class)).value();
//            try {
//                targetType = GlobTypeUtils.getType(targetClass);
//            } catch (InvalidParameter e) {
//                throw new InvalidParameter("LinkField '" + name + "' in type '" + globType.getName() +
//                        "' cannot reference target class '" + targetClass.getName() + "' because "
//                        +
//                        "it does not define a Glob type");
//            }
//            if (targetType == null) {
//                throw new InvalidParameter("Is GlobTypeLoader.init() of " + targetClass + " called from static init?");
//            }
//            Field[] keyFields = targetType.getKeyFields();
//            if (keyFields.length != 1) {
//                throw new InvalidParameter("LinkField '" + name + "' in type '" + globType.getName() +
//                        "' cannot reference target type '" + targetType.getName() +
//                        "' because it uses a composite key");
//            }
//            Field field = keyFields[0];
//            if (!(field instanceof IntegerField)) {
//                throw new InvalidParameter("LinkField '" + name + "' in type '" + globType.getName() +
//                        "' cannot reference target type '" + targetType.getName() +
//                        "' because it does not use an integer key");
//            }
//            targetKeyField = (IntegerField) field;
//            setDefaultValue(computeDefaultValue(this, annotations, DefaultInteger.class));
//
//        }
//
//        public IntegerField getTargetKeyField() {
//            return targetKeyField;
//        }
//
//        public void visit(FieldVisitor visitor) throws Exception {
//            visitor.visitLink(this);
//        }
//
//        public void safeVisit(FieldVisitor visitor) {
//            try {
//                visitor.visitLink(this);
//            } catch (RuntimeException e) {
//                throw new RuntimeException("On " + this, e);
//            } catch (Exception e) {
//                throw new UnexpectedApplicationState("On " + this, e);
//            }
//        }
//
//        public void safeVisit(FieldValueVisitor visitor, Object value) {
//            try {
//                visitor.visitInteger(this, (Integer) value);
//            } catch (RuntimeException e) {
//                throw new RuntimeException("On " + this, e);
//            } catch (Exception e) {
//                throw new UnexpectedApplicationState("On " + this, e);
//            }
//        }
//
//        public GlobType getSourceType() {
//            return getGlobType();
//        }
//
//        public GlobType getTargetType() {
//            return targetType;
//        }
//
//        public void apply(FieldMappingFunctor functor) {
//            functor.process(this, targetKeyField);
//        }
//
//        public void visit(LinkVisitor linkVisitor) {
//            linkVisitor.visitLink(this);
//        }
//
//        public String toString() {
//            return DefaultLink.toString(getName(), getSourceType(), getTargetType());
//        }
//    }


//    private static Object computeDefaultValue(Field field,
//                                              Annotations annotations,
//                                              Key targetAnnotationClass) {
//        Glob glob = annotations.findAnnotation(targetAnnotationClass);
//        if (glob != null) {
//            Field fieldWithValue = glob.getType().getFieldWithAnnotation(DefaultFieldValueType.key);
//            return glob.getValue(fieldWithValue);
//        }
//        for (Class annotationClass : defaultValuesAnnotations) {
//            Annotation annotation = annotations.get(annotationClass);
//            if (annotation == null) {
//                continue;
//            }
//            if (!annotationClass.equals(targetAnnotationClass)) {
//                throw new InvalidParameter("Field " + field.getGlobType().getName() + "." + field.getName() +
//                        " should declare a default value with annotation @" +
//                        targetAnnotationClass.getSimpleName() +
//                        " instead of @" + annotationClass.getSimpleName());
//            }
//            if (targetAnnotationClass.equals(DefaultDate.class)) {
//                return new Date();
//            }
//            try {
//                return annotationClass.getMethod("value").invoke(annotation);
//            } catch (Exception e) {
//                System.out.println("on type : '" + field.getGlobType().getName() + "'; on field '" + field.getName() + "'");
//                e.printStackTrace();
//                throw new InvalidParameter("Cannot determine default value for field: " + field, e);
//            }
//        }
//        return null;
//    }
}
