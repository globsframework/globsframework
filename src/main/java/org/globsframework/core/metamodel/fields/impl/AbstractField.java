package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.annotations.Required;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.Utils;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

abstract public class AbstractField {
    /**
     * The view of a {@link Field} that core's hot paths go through ({@code AbstractDefaultGlob}, {@code FieldCheck}).
     * On it {@code getIndex}, {@code getGlobType}, {@code isKeyField} and {@link #fastCheckValue} are final, hence
     * statically bound, where on {@code Field} each is an itable dispatch at any call site that sees several field
     * classes — measured ×3 on a loop of {@code getIndex} over five field classes, and ×4.7 on {@code getValue} with
     * the checks on. Every Field implementation in globsframework extends this class; the scalar sub-interfaces
     * ({@code IntegerField}, …) are {@code non-sealed} though, so a Field implemented elsewhere would fail here
     * with a ClassCastException.
     */
    public static AbstractField impl(Field field) {
        return (AbstractField) field;
    }

    private final int index;
    private final int keyIndex;
    //    private final Supplier<GlobType> globTypeSupplier;
    private GlobType globType;
    private final String name;
    private final Class valueClass;
    private final Object defaultValue;
    private final DataType dataType;
    // true when checkValue is nothing more than the exact-class test: every field but the four Glob-valued ones,
    // which override it to check the target type(s) — and a Glob[] passes the class test while its elements may not
    private final boolean exactClassCheck;
    private Map<Class<?>, Object> registered = null;
    private final HashMap<Key, Glob> annotations;

    protected AbstractField(String name, Supplier<GlobType> globTypeSupplier,
                            Class valueClass, int index, int keyIndex, boolean isKeyField,
                            Object defaultValue, DataType dataType, HashMap<Key, Glob> annotations) {
        this.annotations = annotations;
        this.keyIndex = isKeyField ? keyIndex : -1;
        this.defaultValue = defaultValue;
        this.name = name;
        this.index = index;
//        this.globTypeSupplier = LazyConstant.of(globTypeSupplier);
        this.valueClass = valueClass;
        this.dataType = dataType;
        this.exactClassCheck = valueClass != Glob.class && valueClass != Glob[].class;
    }

    public void typeComplete(GlobType globType) {
//        if ( globType != globTypeSupplier.get()) {
//            throw new IllegalStateException("GlobType changed for field " + this);
//        }
        this.globType = globType;
//        globType.get();
    }

    public Object normalize(Object value) {
        return value;
    }

    final public String getName() {
        return name;
    }

    final public String getFullName() {
//        return globTypeSupplier.get().getName() + "." + name;
        return globType.getName() + "." + name;
    }

    final public GlobType getGlobType() {
//        return globTypeSupplier.get();
        return globType;
    }

    final public int getIndex() {
        return index;
    }

    final public int getKeyIndex() {
        return keyIndex;
    }

    final public boolean isKeyField() {
        return keyIndex != -1;
    }

    final public boolean isRequired() {
        return hasAnnotation(Required.UNIQUE_KEY);
    }

    final public DataType getDataType() {
        return dataType;
    }

    public boolean checkValue(Object object) throws InvalidParameter {
        if ((object != null) && (valueClass != object.getClass())) {
            throw new InvalidParameter("Value '" + object + "' (" + object.getClass().getName()
                                       + ") is not authorized for field: " + getName() +
                                       " (expected " + valueClass.getName() + ")");
        }
        return true;
    }

    /**
     * {@link #checkValue} for the hot paths of core, which reach a field through a cast to this class: being final it
     * is statically bound, so a call site seeing every field class does not pay an itable dispatch for the common
     * case — a null, or a value of exactly the field's class. Anything else, and every Glob-valued field, goes to the
     * virtual {@code checkValue}, which either accepts it or throws.
     */
    public final boolean fastCheckValue(Object object) {
        if (object == null || (exactClassCheck && object.getClass() == valueClass)) {
            return true;
        }
        return checkValue(object);
    }

    final public Class getValueClass() {
        return valueClass;
    }

    final public Object getDefaultValue() {
        return defaultValue;
    }

    public String toString() {
//        return globTypeSupplier.get().getName() + "." + name;
        return globType.getName() + "." + name;
    }

    public boolean valueEqual(Object o1, Object o2) {
        return Utils.equal(o1, o2);
    }

    public boolean valueOrKeyEqual(Object o1, Object o2) {
        return valueEqual(o1, o2);
    }

    public int valueHash(Object o) {
        return o.hashCode();
    }

    public void toString(StringBuilder buffer, Object value) {
        buffer.append(value);
    }

    public <T> void register(Class<T> klass, T t) {
        if (registered == null) {
            registered = new HashMap<>(4);
        }
        registered.put(klass, t);
    }

    public <T> T getRegistered(Class<T> klass) {
        return registered == null ? null : (T) registered.get(klass);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return false;
    }

    public Stream<Glob> streamAnnotations() {
        return annotations.values().stream();
    }

    public Stream<Glob> streamAnnotations(GlobType type) {
        return annotations.values().stream().filter(glob -> glob.getType() == type);
    }

    public boolean hasAnnotation(Key key) {
        return annotations.containsKey(key);
    }

    public Glob getAnnotation(Key key) {
        Glob annotation = annotations.get(key);
        if (annotation == null) {
            throw new ItemNotFound(key.toString() + " on " + toString());
        }
        return annotation;
    }

    public Glob findAnnotation(Key key) {
        return annotations.get(key);
    }

}
