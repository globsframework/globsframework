package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.*;

import java.util.function.Supplier;

/**
 * A field being declared, for what does not fit in its annotations: the objects registered on it
 * ({@link Field#getRegistered}). They are handed to the field when it is built and cannot change afterwards.
 * <p>
 * Obtained from {@link GlobTypeBuilder#buildField}, and ended by exactly one {@code asXxxField}, which declares
 * the field on the type builder and returns it, as the matching {@code declareXxxField} would.
 */
public interface FieldBuilder {

    <T> FieldBuilder register(Class<T> klass, T instance);

    StringField asStringField();

    StringArrayField asStringArrayField();

    IntegerField asIntegerField();

    IntegerArrayField asIntegerArrayField();

    DoubleField asDoubleField();

    DoubleArrayField asDoubleArrayField();

    LongField asLongField();

    LongArrayField asLongArrayField();

    BigDecimalField asBigDecimalField();

    BigDecimalArrayField asBigDecimalArrayField();

    BooleanField asBooleanField();

    BooleanArrayField asBooleanArrayField();

    DateField asDateField();

    DateTimeField asDateTimeField();

    BytesField asBytesField();

    <T> GlobField<T> asGlobField(Supplier<GlobType> globType);

    <T> GlobArrayField<T> asGlobArrayField(Supplier<GlobType> globType);

    GlobUnionField asGlobUnionField(Supplier<GlobType>[] types);

    GlobArrayUnionField asGlobUnionArrayField(Supplier<GlobType>[] types);
}
