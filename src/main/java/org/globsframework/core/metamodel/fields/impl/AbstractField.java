package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.annotations.Required;
import org.globsframework.core.metamodel.impl.AbstractDefaultAnnotations;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.Utils;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.InvalidParameter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

abstract public class AbstractField implements AbstractDefaultAnnotations {
    private final int index;
    private final int keyIndex;
    private final Supplier<GlobType> globTypeSupplier;
    private GlobType globType;
    private final String name;
    private final Class valueClass;
    private final Object defaultValue;
    private final DataType dataType;
    private Map<Class<?>, Object> registered = null;
    private final boolean keyField;
    private final HashContainer<Key, Glob> annotations;

    protected AbstractField(String name, Supplier<GlobType> globTypeSupplier,
                            Class valueClass, int index, int keyIndex, boolean isKeyField,
                            Object defaultValue, DataType dataType, HashContainer<Key, Glob> annotations) {
        this.annotations = annotations;
        this.keyIndex = keyIndex;
        this.defaultValue = defaultValue;
        this.name = name;
        this.keyField = isKeyField;
        this.index = index;
        this.globTypeSupplier = globTypeSupplier; //StableValue.supplier(globType);
        this.valueClass = valueClass;
        this.dataType = dataType;
    }

    public void typeComplete() {
        globType = globTypeSupplier.get();
    }

    public Object normalize(Object value) {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return globType.getName() + "." + name;
    }

    public GlobType getGlobType() {
        return globType;
    }

    public int getIndex() {
        return index;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public boolean isKeyField() {
        return keyField;
    }

    public boolean isRequired() {
        return hasAnnotation(Required.UNIQUE_KEY);
    }

    public DataType getDataType() {
        return dataType;
    }

    public void checkValue(Object object) throws InvalidParameter {
        if ((object != null) && (!valueClass.equals(object.getClass()))) {
            throw new InvalidParameter("Value '" + object + "' (" + object.getClass().getName()
                                       + ") is not authorized for field: " + getName() +
                                       " (expected " + valueClass.getName() + ")");
        }
    }

    public Class getValueClass() {
        return valueClass;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String toString() {
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
            registered = new HashMap<>();
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractField other = (AbstractField) o;
        return name.equals(other.name) && globType.equals(other.globType);
    }

    @Override
    public HashContainer<Key, Glob> getAnnotations() {
        return annotations;
    }
}
