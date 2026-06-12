package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public final class ZeroFieldMutableKey extends AbstractFieldValue<MutableFunctionalKey>
        implements MutableFunctionalKey, FunctionalKey {
    private final ZeroFunctionalKeyBuilder functionalKeyBuilder;

    public ZeroFieldMutableKey(ZeroFunctionalKeyBuilder functionalKeyBuilder) {
        this.functionalKeyBuilder = functionalKeyBuilder;
    }

    protected MutableFunctionalKey doSet(Field field, Object o) {
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    protected Object doGet(Field field) {
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public FunctionalKey getShared() {
        return this;
    }

    public FunctionalKey create() {
        return new ZeroFieldMutableKey(functionalKeyBuilder);
    }

    public void unset(Field field) {
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public boolean contains(Field field) {
        return false;
    }

    public int size() {
        return 1;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        return functor;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T visitor, CTX ctx) throws Exception {
        return visitor;
    }

    public FieldValue[] toArray() {
        return EMPTY.toArray();
    }

    public FunctionalKeyBuilder getBuilder() {
        return functionalKeyBuilder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ZeroFieldMutableKey that) {
            return that.functionalKeyBuilder.getType() == functionalKeyBuilder.getType();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return functionalKeyBuilder.hash;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public String toString() {
        return functionalKeyBuilder.type.getName();
    }
}
