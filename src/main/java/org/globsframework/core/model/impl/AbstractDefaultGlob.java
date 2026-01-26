package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Arrays;

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
            final int index = field.getIndex();
            if (isSetAt(index)) { //  || field.isKeyField()
                field.acceptValue(functor, values[index]);
            }
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        for (Field field : type.getFields()) {
            final int index = field.getIndex();
            if (isSetAt(index)) {
                field.acceptValue(functor, values[index], ctx);
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

    public boolean isNull(int index) {
        return values[index] == null;
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
        if (!Glob.class.isAssignableFrom(o.getClass())) {
            return otherKey.equals(this);
        }

        if (getType() != otherKey.getGlobType()) {
            return false;
        }

        Field[] keyFields = getType().getKeyFields();
        for (Field field : keyFields) {
            if (!field.valueEqual(getValue(field), otherKey.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MutableGlob getMutable(GlobField field) throws ItemNotFound {
        return getMutableGlob(get(field), field);
    }

    @Override
    public MutableGlob[] getMutable(GlobArrayField field) throws ItemNotFound {
        return getMutableGlobs(get(field), field);
    }

    @Override
    public MutableGlob getMutable(GlobUnionField field) throws ItemNotFound {
        return getMutableGlob(get(field), field);
    }

    private MutableGlob getMutableGlob(Glob glob, Field field) {
        if (glob == null || glob instanceof MutableGlob) {
            return (MutableGlob) glob;
        }
        throw new ClassCastException(glob.getClass().getName() + " is not mutable on field " + field.getName());
    }

    @Override
    public MutableGlob[] getMutable(GlobArrayUnionField field) throws ItemNotFound {
        return getMutableGlobs(get(field), field);
    }

    private MutableGlob[] getMutableGlobs(Glob[] globs, Field field) {
        if (globs != null) {
            if (globs instanceof MutableGlob[]) {
                return (MutableGlob[]) globs;
            }
            else {
                if (FieldCheck.CheckGlob.shouldCheck) {
                    for (Glob value : globs) {
                        if (value != null && !(value instanceof MutableGlob)) {
                            throw new ClassCastException(value.getClass().getName() + " is not mutable on field " + field.getName());
                        }
                    }
                }
                final MutableGlob[] value = Arrays.copyOfRange(globs, 0, globs.length, MutableGlob[].class);
                setObject(field, value);
                return value;
            }
        } else {
            return null;
        }
    }
}
