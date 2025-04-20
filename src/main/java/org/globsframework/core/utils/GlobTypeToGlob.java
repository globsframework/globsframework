package org.globsframework.core.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeResolver;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.metamodel.type.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;

import java.util.*;
import java.util.stream.Collectors;

public class GlobTypeToGlob {

    public static Collection<Glob> toGlob(GlobType type) {
        return toGlob(type, new HashSet<>());
    }

    private static Collection<Glob> toGlob(GlobType type, Set<GlobType> done) {
        if (done.contains(type)) {
            return Collections.emptyList();
        }
        done.add(type);
        final MutableGlob t = GlobTypeType.TYPE.instantiate()
                .set(GlobTypeType.kind, type.getName());
        List<Glob> types = new ArrayList<>();
        types.add(t);

        List<Glob> fields = new ArrayList<>(type.getFieldCount());
        for (Field field : type.getFields()) {
            fields.add(
                    extractAnnotation(done, field, types)
                    );
        }
        t.set(GlobTypeType.fields, fields.toArray(new Glob[0]));
        t.set(GlobTypeType.annotations, type.streamAnnotations().toArray(Glob[]::new));
        return types;
    }

    private static Glob extractAnnotation(Set<GlobType> done, Field field, List<Glob> types) {
        Glob annotation;
        switch (field.getDataType()) {
            case String: {
                annotation = extractAnnotation(field, StringFieldType.create(field.getName()));
                break;
            }
            case StringArray: {
                annotation = extractAnnotation(field, StringArrayFieldType.create(field.getName()));
                break;
            }
            case Double: {
                annotation = extractAnnotation(field, DoubleFieldType.create(field.getName()));
                break;
            }
            case DoubleArray: {
                annotation = extractAnnotation(field, DoubleArrayFieldType.create(field.getName()));
                break;
            }
            case BigDecimal: {
                annotation = extractAnnotation(field, BigDecimalFieldType.create(field.getName()));
                break;
            }
            case BigDecimalArray: {
                annotation = extractAnnotation(field, BigDecimalArrayFieldType.create(field.getName()));
                break;
            }
            case Long: {
                annotation = extractAnnotation(field, LongFieldType.create(field.getName()));
                break;
            }
            case LongArray:{
                annotation = extractAnnotation(field, LongArrayFieldType.create(field.getName()));
                break;
            }
            case Integer: {
                annotation = extractAnnotation(field, IntegerFieldType.create(field.getName()));
                break;
            }
            case IntegerArray: {
                annotation = extractAnnotation(field, IntegerArrayFieldType.create(field.getName()));
            break;
            }
            case Boolean: {
                annotation = extractAnnotation(field, BooleanFieldType.create(field.getName()));
            break;
            }
            case BooleanArray: {
                annotation = extractAnnotation(field, BooleanArrayFieldType.create(field.getName()));
                break;
            }
            case Date: {
                annotation = extractAnnotation(field, DateFieldType.create(field.getName()));
                break;
            }
            case DateTime: {
                annotation = extractAnnotation(field, DateTimeFieldType.create(field.getName()));
            break;
            }
            case Bytes: {
                annotation = extractAnnotation(field, BytesFieldType.create(field.getName()));
                break;
            }
            case Glob:  {
                final GlobType targetType = ((GlobField) field).getTargetType();
                final MutableGlob set = extractAnnotation(field, GlobFieldType.create(field.getName()))
                        .set(GlobFieldType.targetType, targetType.getName());
                types.addAll(toGlob(targetType, done));
                return set;
            }
            case GlobArray: {
                final GlobType targetType = ((GlobArrayField) field).getTargetType();
                final MutableGlob set = extractAnnotation(field, GlobArrayFieldType.create(field.getName()))
                        .set(GlobArrayFieldType.targetType, targetType.getName());
                types.addAll(toGlob(targetType, done));
                return set;
            }
            case GlobUnion: {
                final Collection<GlobType> targetTypes = ((GlobUnionField) field).getTargetTypes();
                final MutableGlob set = extractAnnotation(field, GlobUnionFieldType.create(field.getName()))
                        .set(GlobUnionFieldType.targetTypes, targetTypes.stream().map(GlobType::getName).toArray(String[]::new));
                targetTypes.forEach(globType -> types.addAll(toGlob(globType, done)));
                return set;
            }
            case GlobUnionArray: {
                final Collection<GlobType> targetTypes = ((GlobArrayUnionField) field).getTargetTypes();
                final MutableGlob set = extractAnnotation(field, GlobUnionArrayFieldType.create(field.getName()))
                        .set(GlobUnionArrayFieldType.targetTypes, targetTypes.stream().map(GlobType::getName).toArray(String[]::new));
                targetTypes.forEach(globType -> types.addAll(toGlob(globType, done)));
                return set;
            }
            default:
                throw new RuntimeException("Unknown type " + field.getDataType());
        }
        return annotation;
    }

    private static MutableGlob extractAnnotation(Field field, MutableGlob mutableGlob) {
        GlobArrayUnionField annotations = mutableGlob.getType().getField("annotations").asGlobArrayUnionField();
        // force add annotation type to the global liste of type possible for String
        field.streamAnnotations().map(Glob::getType).forEach(annotations::__add__);
        mutableGlob.set(annotations, field.streamAnnotations().toArray(Glob[]::new));
        return mutableGlob;
    }

    public static Collection<GlobType> fromGlob(Collection<Glob> main, GlobTypeResolver globTypeResolver) {
        Map<String, GlobTypeBuilder> onGoing = new HashMap<>();
        for (Glob glob : main) {
            final String kind = glob.getNotNull(GlobTypeType.kind);
            onGoing.put(kind, new DefaultGlobTypeBuilder(kind));
        }
        for (Glob t : main) {
            extractGlobType(onGoing, t, globTypeResolver);
        }
        return main.stream().map(glob -> {
            final String k = glob.getNotNull(GlobTypeType.kind);
            return onGoing.get(k).get();
        }).collect(Collectors.toList());
    }

    private static GlobType extractGlobType(Map<String, GlobTypeBuilder> onGoing, Glob t, GlobTypeResolver globTypeResolver) {
        final String kind = t.getNotNull(GlobTypeType.kind);
        final GlobTypeBuilder globTypeBuilder = onGoing.get(kind);
        final Glob[] fields = t.getOrEmpty(GlobTypeType.fields);
        for (Glob field : fields) {
            final GlobType type = field.getType();
            if (type == StringFieldType.TYPE) {
                globTypeBuilder.addStringField(field.get(StringFieldType.name), Arrays.asList(field.getOrEmpty(StringFieldType.annotations)));
            } else if (type == StringArrayFieldType.TYPE) {
                globTypeBuilder.addStringArrayField(field.get(StringArrayFieldType.name), Arrays.asList(field.getOrEmpty(StringArrayFieldType.annotations)));
            } else if (type == BooleanFieldType.TYPE) {
                globTypeBuilder.addBooleanField(field.get(BooleanFieldType.name), Arrays.asList(field.getOrEmpty(BooleanFieldType.annotations)));
            } else if (type == BooleanArrayFieldType.TYPE) {
                globTypeBuilder.addBooleanArrayField(field.get(BooleanArrayFieldType.name), Arrays.asList(field.getOrEmpty(BooleanArrayFieldType.annotations)));
            } else if (type == IntegerFieldType.TYPE) {
                globTypeBuilder.addIntegerField(field.get(IntegerFieldType.name), Arrays.asList(field.getOrEmpty(IntegerFieldType.annotations)));
            } else if (type == IntegerArrayFieldType.TYPE) {
                globTypeBuilder.addIntegerArrayField(field.get(IntegerArrayFieldType.name), Arrays.asList(field.getOrEmpty(IntegerArrayFieldType.annotations)));
            } else if (type == DoubleFieldType.TYPE) {
                globTypeBuilder.addDoubleField(field.get(DoubleFieldType.name), Arrays.asList(field.getOrEmpty(DoubleFieldType.annotations)));
            } else if (type == DoubleArrayFieldType.TYPE) {
                globTypeBuilder.addDoubleArrayField(field.get(DoubleArrayFieldType.name), Arrays.asList(field.getOrEmpty(DoubleArrayFieldType.annotations)));
            } else if (type == LongFieldType.TYPE) {
                globTypeBuilder.addLongField(field.get(LongFieldType.name), Arrays.asList(field.getOrEmpty(LongFieldType.annotations)));
            } else if (type == LongArrayFieldType.TYPE) {
                globTypeBuilder.addLongArrayField(field.get(LongArrayFieldType.name), Arrays.asList(field.getOrEmpty(LongArrayFieldType.annotations)));
            } else if (type == BigDecimalFieldType.TYPE) {
                globTypeBuilder.addBigDecimalField(field.get(BigDecimalFieldType.name), Arrays.asList(field.getOrEmpty(BigDecimalFieldType.annotations)));
            } else if (type == BigDecimalArrayFieldType.TYPE) {
                globTypeBuilder.addBigDecimalArrayField(field.get(BigDecimalArrayFieldType.name), Arrays.asList(field.getOrEmpty(BigDecimalArrayFieldType.annotations)));
            } else if (type == DateFieldType.TYPE) {
                globTypeBuilder.addDateField(field.get(DateFieldType.name), Arrays.asList(field.getOrEmpty(DateFieldType.annotations)));
            } else if (type == DateTimeFieldType.TYPE) {
                globTypeBuilder.addDateTimeField(field.get(DateTimeFieldType.name), Arrays.asList(field.getOrEmpty(DateTimeFieldType.annotations)));
            } else if (type == BytesFieldType.TYPE) {
                globTypeBuilder.addBlobField(field.get(BytesFieldType.name), Arrays.asList(field.getOrEmpty(BytesFieldType.annotations)));
            } else if (type == GlobFieldType.TYPE) {
                final String targetKey = field.get(GlobFieldType.targetType);
                final GlobTypeBuilder targetTypeBuilder = onGoing.get(targetKey);
                globTypeBuilder.addGlobField(field.get(GlobFieldType.name), Arrays.asList(field.getOrEmpty(GlobFieldType.annotations)),
                        targetTypeBuilder != null ? targetTypeBuilder.unCompleteType() : globTypeResolver.getType(targetKey));
            } else if (type == GlobUnionFieldType.TYPE) {
                final String[] targetTypes = field.getOrEmpty(GlobUnionFieldType.targetTypes);
                List<GlobType> globTypes = Arrays.stream(targetTypes).map(s -> {
                    final GlobTypeBuilder targetTypeBuilder = onGoing.get(s);
                    return targetTypeBuilder != null ? targetTypeBuilder.unCompleteType() : globTypeResolver.getType(s);
                }).collect(Collectors.toList());
                globTypeBuilder.addUnionGlobField(field.get(GlobUnionFieldType.name),
                        Arrays.asList(field.getOrEmpty(GlobUnionFieldType.annotations)), globTypes);
            } else if (type == GlobArrayFieldType.TYPE) {
                final String targetKey = field.get(GlobArrayFieldType.targetType);
                final GlobTypeBuilder targetTypeBuilder = onGoing.get(targetKey);
                globTypeBuilder.addGlobArrayField(field.get(GlobArrayFieldType.name), Arrays.asList(field.getOrEmpty(GlobArrayFieldType.annotations)),
                        targetTypeBuilder != null ? targetTypeBuilder.unCompleteType() : globTypeResolver.getType(targetKey));
            } else if (type == GlobUnionArrayFieldType.TYPE) {
                final String[] targetTypes = field.getOrEmpty(GlobUnionArrayFieldType.targetTypes);
                List<GlobType> globTypes = Arrays.stream(targetTypes).map(s -> {
                    final GlobTypeBuilder targetTypeBuilder = onGoing.get(s);
                    return targetTypeBuilder != null ? targetTypeBuilder.unCompleteType() : globTypeResolver.getType(s);
                }).collect(Collectors.toList());
                globTypeBuilder.addUnionGlobField(field.get(GlobUnionArrayFieldType.name),
                        Arrays.asList(field.getOrEmpty(GlobUnionArrayFieldType.annotations)), globTypes);
            } else {
                throw new RuntimeException("Unknown type " + type.getName());
            }
        }
        return globTypeBuilder.get();
    }

}
