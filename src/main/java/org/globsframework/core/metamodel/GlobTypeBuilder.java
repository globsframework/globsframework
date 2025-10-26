package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface GlobTypeBuilder {
    GlobTypeBuilder addAnnotation(Glob annotation);

    GlobTypeBuilder addAnnotations(Collection<Glob> annotation);

    GlobTypeBuilder addStringField(String fieldName, Collection<Glob> annotations);

    GlobTypeBuilder addStringArrayField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addIntegerField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addIntegerArrayField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addDoubleField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addDoubleArrayField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addBigDecimalField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addBigDecimalArrayField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addDateField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addDateTimeField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addLongField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addLongArrayField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addBooleanField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addBooleanArrayField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addBlobField(String fieldName, Collection<Glob> globAnnotations);

    GlobTypeBuilder addGlobField(String fieldName, Collection<Glob> globAnnotations, GlobType type);

    GlobTypeBuilder addGlobArrayField(String fieldName, Collection<Glob> globAnnotations, GlobType type);

    GlobTypeBuilder addUnionGlobField(String fieldName, Collection<Glob> globAnnotations, List<GlobType> types);

    GlobTypeBuilder addUnionGlobArrayField(String fieldName, Collection<Glob> globAnnotations, List<GlobType> types);

    StringField declareStringField(String fieldName, Collection<Glob> annotations);

    StringArrayField declareStringArrayField(String fieldName, Collection<Glob> globAnnotations);

    IntegerField declareIntegerField(String fieldName, Collection<Glob> annotations);

    DoubleArrayField declareDoubleArrayField(String fieldName, Collection<Glob> annotations);

    BooleanField declareBooleanField(String fieldName, Collection<Glob> annotations);

    BooleanArrayField declareBooleanArrayField(String fieldName, Collection<Glob> annotations);

    IntegerArrayField declareIntegerArrayField(String fieldName, Collection<Glob> annotations);

    DoubleField declareDoubleField(String fieldName, Collection<Glob> annotations);

    BigDecimalField declareBigDecimalField(String fieldName, Collection<Glob> annotations);

    BigDecimalArrayField declareBigDecimalArrayField(String fieldName, Collection<Glob> annotations);

    DateField declareDateField(String fieldName, Collection<Glob> annotations);

    DateTimeField declareDateTimeField(String fieldName, Collection<Glob> annotations);

    LongField declareLongField(String fieldName, Collection<Glob> annotations);

    LongArrayField declareLongArrayField(String fieldName, Collection<Glob> annotations);

    BlobField declareBlobField(String fieldName, Collection<Glob> annotations);

    GlobField declareGlobField(String fieldName, GlobType globType, Collection<Glob> annotations);

    GlobArrayField declareGlobArrayField(String fieldName, GlobType globType, Collection<Glob> annotations);

    GlobUnionField declareGlobUnionField(String fieldName, Collection<GlobType> types, Collection<Glob> annotations);

    GlobArrayUnionField declareGlobUnionArrayField(String fieldName, Collection<GlobType> types, Collection<Glob> annotations);

    Field declare(String fieldName, DataType dataType, Collection<Glob> annotations);

    Field declareFrom(String fieldName, Field field);

    default GlobTypeBuilder addStringField(String fieldName, Glob... annotations) {
        return addStringField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addIntegerField(String fieldName, Glob... annotations) {
        return addIntegerField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addDoubleField(String fieldName, Glob... annotations) {
        return addDoubleField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addLongField(String fieldName, Glob... annotations) {
        return addLongField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addIntegerArrayField(String fieldName, Glob... annotations) {
        return addIntegerArrayField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addLongArrayField(String fieldName, Glob... annotations) {
        return addLongArrayField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addBooleanField(String fieldName, Glob... annotations) {
        return addBooleanField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addBooleanArrayField(String fieldName, Glob... annotations) {
        return addBooleanArrayField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addBigDecimalField(String fieldName, Glob... annotations) {
        return addBigDecimalField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addBigDecimalArrayField(String fieldName, Glob... annotations) {
        return addBigDecimalArrayField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addDateField(String fieldName, Glob... annotations) {
        return addDateField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addDateTimeField(String fieldName, Glob... annotations) {
        return addDateTimeField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addDoubleArrayField(String fieldName, Glob... annotations) {
        return addDoubleArrayField(fieldName, Arrays.asList(annotations));
    }

    default GlobTypeBuilder addBlobField(String fieldName, Glob... annotations) {
        return addBlobField(fieldName, Arrays.asList(annotations));
    }

    default StringField declareStringField(String fieldName, Glob... annotations) {
        return declareStringField(fieldName, Arrays.asList(annotations));
    }

    default StringArrayField declareStringArrayField(String fieldName, Glob... annotations) {
        return declareStringArrayField(fieldName, Arrays.asList(annotations));
    }

    default IntegerField declareIntegerField(String fieldName, Glob... annotations) {
        return declareIntegerField(fieldName, Arrays.asList(annotations));
    }

    default IntegerArrayField declareIntegerArrayField(String fieldName, Glob... annotations) {
        return declareIntegerArrayField(fieldName, Arrays.asList(annotations));
    }

    default BooleanField declareBooleanField(String fieldName, Glob... annotations) {
        return declareBooleanField(fieldName, Arrays.asList(annotations));
    }

    default BooleanArrayField declareBooleanArrayField(String fieldName, Glob... annotations) {
        return declareBooleanArrayField(fieldName, Arrays.asList(annotations));
    }

    default DoubleField declareDoubleField(String fieldName, Glob... annotations) {
        return declareDoubleField(fieldName, Arrays.asList(annotations));
    }

    default DoubleArrayField declareDoubleArrayField(String fieldName, Glob... annotations) {
        return declareDoubleArrayField(fieldName, Arrays.asList(annotations));
    }

    default LongField declareLongField(String fieldName, Glob... annotations) {
        return declareLongField(fieldName, Arrays.asList(annotations));
    }

    default BigDecimalField declareBigDecimalField(String fieldName, Glob... annotations) {
        return declareBigDecimalField(fieldName, Arrays.asList(annotations));
    }

    default BigDecimalArrayField declareBigDecimalArrayField(String fieldName, Glob... annotations) {
        return declareBigDecimalArrayField(fieldName, Arrays.asList(annotations));
    }

    default BlobField declareBlobField(String fieldName, Glob... annotations) {
        return declareBlobField(fieldName, Arrays.asList(annotations));
    }

    default LongArrayField declareLongArrayField(String fieldName, Glob... annotations) {
        return declareLongArrayField(fieldName, Arrays.asList(annotations));
    }

    default DateField declareDateField(String fieldName, Glob... annotations) {
        return declareDateField(fieldName, Arrays.asList(annotations));
    }

    default DateTimeField declareDateTimeField(String fieldName, Glob... annotations) {
        return declareDateTimeField(fieldName, Arrays.asList(annotations));
    }

    default GlobField declareGlobField(String fieldName, GlobType globType, Glob... annotations) {
        return declareGlobField(fieldName, globType, Arrays.asList(annotations));
    }

    default GlobArrayField declareGlobArrayField(String fieldName, GlobType globType, Glob... annotations) {
        return declareGlobArrayField(fieldName, globType, Arrays.asList(annotations));
    }

    default GlobUnionField declareGlobUnionField(String fieldName, List<GlobType> types, Glob... annotations) {
        return declareGlobUnionField(fieldName, types, Arrays.asList(annotations));
    }

    default GlobArrayUnionField declareGlobUnionArrayField(String fieldName, List<GlobType> types, Glob... annotations) {
        return declareGlobUnionArrayField(fieldName, types, Arrays.asList(annotations));
    }

    <T> GlobTypeBuilder register(Class<T> klass, T t);

    <T> GlobTypeBuilder register(Field field, Class<T> klass, T t);

    GlobType get();

    GlobType unCompleteType();

    void complete(); // call

    default GlobTypeBuilder addIntegerKey(String fieldName) {
        addIntegerField(fieldName, KeyField.UNINITIALIZED);
        return this;
    }

    boolean isKnown(String fieldName);

}
