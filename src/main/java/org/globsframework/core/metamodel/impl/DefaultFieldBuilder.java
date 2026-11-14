package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.FieldBuilder;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Collects what is registered on a field, then has {@link DefaultGlobTypeBuilder} create the field with it: the
 * field receives its registered objects in its constructor, which is what lets them be final on AbstractField.
 */
final class DefaultFieldBuilder implements FieldBuilder {
    private final DefaultGlobTypeBuilder typeBuilder;
    private final String name;
    private final Collection<Glob> annotations;
    private Map<Class<?>, Object> registered;
    private boolean built;

    DefaultFieldBuilder(DefaultGlobTypeBuilder typeBuilder, String name, Collection<Glob> annotations) {
        this.typeBuilder = typeBuilder;
        this.name = name;
        this.annotations = annotations;
    }

    public <T> FieldBuilder register(Class<T> klass, T instance) {
        checkNotBuilt();
        Objects.requireNonNull(klass, "registered class");
        Objects.requireNonNull(instance, "registered instance");
        if (registered == null) {
            registered = new HashMap<>(4);
        }
        registered.put(klass, instance);
        return this;
    }

    /** the registered objects, once: a second asXxxField would declare a second field of the same name */
    private Map<Class<?>, Object> build() {
        checkNotBuilt();
        built = true;
        return registered;
    }

    private void checkNotBuilt() {
        if (built) {
            throw new IllegalStateException("Field '" + name + "' is already built");
        }
    }

    public StringField asStringField() {
        return typeBuilder.createStringField(name, annotations, build());
    }

    public StringArrayField asStringArrayField() {
        return typeBuilder.createStringArrayField(name, annotations, build());
    }

    public IntegerField asIntegerField() {
        return typeBuilder.createIntegerField(name, annotations, build());
    }

    public IntegerArrayField asIntegerArrayField() {
        return typeBuilder.createIntegerArrayField(name, annotations, build());
    }

    public DoubleField asDoubleField() {
        return typeBuilder.createDoubleField(name, annotations, build());
    }

    public DoubleArrayField asDoubleArrayField() {
        return typeBuilder.createDoubleArrayField(name, annotations, build());
    }

    public LongField asLongField() {
        return typeBuilder.createLongField(name, annotations, build());
    }

    public LongArrayField asLongArrayField() {
        return typeBuilder.createLongArrayField(name, annotations, build());
    }

    public BigDecimalField asBigDecimalField() {
        return typeBuilder.createBigDecimalField(name, annotations, build());
    }

    public BigDecimalArrayField asBigDecimalArrayField() {
        return typeBuilder.createBigDecimalArrayField(name, annotations, build());
    }

    public BooleanField asBooleanField() {
        return typeBuilder.createBooleanField(name, annotations, build());
    }

    public BooleanArrayField asBooleanArrayField() {
        return typeBuilder.createBooleanArrayField(name, annotations, build());
    }

    public DateField asDateField() {
        return typeBuilder.createDateField(name, annotations, build());
    }

    public DateTimeField asDateTimeField() {
        return typeBuilder.createDateTimeField(name, annotations, build());
    }

    public BytesField asBytesField() {
        return typeBuilder.createBytesField(name, annotations, build());
    }

    public <T> GlobField<T> asGlobField(Supplier<GlobType> globType) {
        return typeBuilder.createGlobField(name, globType, annotations, build());
    }

    public <T> GlobArrayField<T> asGlobArrayField(Supplier<GlobType> globType) {
        return typeBuilder.createGlobArrayField(name, globType, annotations, build());
    }

    public GlobUnionField asGlobUnionField(Supplier<GlobType>[] types) {
        return typeBuilder.createGlobUnionField(name, types, annotations, build());
    }

    public GlobArrayUnionField asGlobUnionArrayField(Supplier<GlobType>[] types) {
        return typeBuilder.createGlobUnionArrayField(name, types, annotations, build());
    }
}
