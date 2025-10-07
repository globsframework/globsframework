package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.Objects;

public class OneFieldMutableKey extends AbstractFieldValue<MutableFunctionalKey>
        implements MutableFunctionalKey, FunctionalKey {
    private final OneFunctionalKeyBuilder functionalKeyBuilder;
    private Object value;

    public OneFieldMutableKey(OneFunctionalKeyBuilder functionalKeyBuilder, Object value) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        this.value = value;
    }

    public OneFieldMutableKey(OneFunctionalKeyBuilder functionalKeyBuilder) {
        this.functionalKeyBuilder = functionalKeyBuilder;
    }

    protected MutableFunctionalKey doSet(Field field, Object o) {
        value = o;
        return this;
    }

    protected Object doGet(Field field) {
        return getNotNullValue(value);
    }

    public FunctionalKey getShared() {
        return this;
    }

    public FunctionalKey create() {
        return new OneFieldMutableKey(functionalKeyBuilder, value);
    }

    public void unset(Field field) {
        if (field == functionalKeyBuilder.field) {
            value = NULL_VALUE;
        }
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public boolean contains(Field field) {
        return functionalKeyBuilder.field == field;
    }

    public int size() {
        return 1;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        if (value != NULL_VALUE) {
            functor.process(functionalKeyBuilder.getFields()[0], value);
        }
        return functor;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        if (value != NULL_VALUE) {
            functionalKeyBuilder.getFields()[0].acceptValue(functor, value);
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T visitor, CTX ctx) throws Exception {
        if (value != NULL_VALUE) {
            functionalKeyBuilder.getFields()[0].acceptValue(visitor, value, ctx);
        }
        return visitor;
    }

    public FieldValue[] toArray() {
        if (value == NULL_VALUE) {
            return EMPTY.toArray();
        }
        return new FieldValue[]{new FieldValue(functionalKeyBuilder.field, value)};
    }

    public FunctionalKeyBuilder getBuilder() {
        return functionalKeyBuilder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof OneFieldMutableKey that) {
            if (functionalKeyBuilder.getType() != that.functionalKeyBuilder.getType()) {
                return false;
            }
            return Objects.equals(value, that.value);
        }
        else {
            return false;
        }
    }

    public int hashCode() {
        return 31 + (value != null ? value.hashCode() : 0);
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return field == functionalKeyBuilder.field && value != NULL_VALUE;
    }

    public String toString() {
        return "OneFieldMutableKey{" +
                "functionalKeyBuilder=" + functionalKeyBuilder +
                ", value=" + value +
                '}';
    }
}
