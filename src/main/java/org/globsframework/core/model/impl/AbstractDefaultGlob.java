package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public abstract class AbstractDefaultGlob implements AbstractMutableGlob {
    protected final GlobType type;
    protected final Object[] values;

    protected AbstractDefaultGlob(GlobType type) {
        this(type, new Object[type.getFieldCount()]);
    }

    public AbstractDefaultGlob(GlobType type, Object[] values) {
        this.type = type;
        this.values = values;
    }

    public GlobType getType() {
        return type;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (Field field : type.getFields()) {
            if (isSet(field)) { //  || field.isKeyField()
                field.accept(functor, values[field.getIndex()]);
            }
        }
        return functor;
    }

    public <T extends FieldValues.Functor>
    T apply(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (isSetAt(index)) {  //  || field.isKeyField()
                functor.process(field, values[index]);
            }
        }
        return functor;
    }

    public Object doGet(Field field) {
        return values[field.getIndex()];
    }

    public Object get(int index) {
        return values[index];
    }

    public void set(int index, Object value) {
        values[index] = value;
        setSetAt(index);
    }

    public MutableGlob doSet(Field field, Object value) {
        int index = field.getIndex();
        values[index] = value;
        setSetAt(index);
        return this;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        final int index = field.getIndex();
        return isSetAt(index);
    }

    abstract public void setSetAt(int index);

    abstract public boolean isSetAt(int index);

    abstract public void clearSetAt(int index);

    public MutableGlob unset(Field field) {
        int index = field.getIndex();
        values[index] = null;
        clearSetAt(index);
        return this;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        toString(buffer);
        return buffer.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!Key.class.isAssignableFrom(o.getClass())) {
            return false;
        }

        Key otherKey = (Key) o;
        if (getType() != otherKey.getGlobType()) {
            return false;
        }

        Field[] keyFields = getType().getKeyFields();
        if (keyFields.length == 0) {
            return true; //o instanceof Glob && reallyEquals((Glob) o);
        }

        for (Field field : keyFields) {
            if (!field.valueEqual(getValue(field), otherKey.getValue(field))) {
                return false;
            }
        }
        return true;
    }

}
