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

public class TwoFieldsMutableKey extends AbstractFieldValue<MutableFunctionalKey>
        implements MutableFunctionalKey, FunctionalKey {
    private final TwoFunctionalKeyBuilder functionalKeyBuilder;
    private Object value1;
    private Object value2;

    TwoFieldsMutableKey(TwoFunctionalKeyBuilder functionalKeyBuilder) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        this.value1 = NULL_VALUE;
        this.value2 = NULL_VALUE;
    }

    TwoFieldsMutableKey(TwoFunctionalKeyBuilder functionalKeyBuilder, Object value1, Object value2) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        this.value1 = value1;
        this.value2 = value2;
    }

    protected MutableFunctionalKey doSet(Field field, Object o) {
        if (functionalKeyBuilder.field1 == field) {
            value1 = o;
        } else {
            value2 = o;
        }
        return this;
    }

    protected Object doGet(Field field) {
        return (field == functionalKeyBuilder.field1 ? getNotNullValue(value1) :
                field == functionalKeyBuilder.field2 ? getNotNullValue(value2) : null);
    }

    public FunctionalKey getShared() {
        return this;
    }

    public FunctionalKey create() {
        return new TwoFieldsMutableKey(functionalKeyBuilder, value1, value2);
    }

    @Override
    public void unset(Field field) {
        value1 = NULL_VALUE;
        value2 = NULL_VALUE;
    }

    public boolean contains(Field field) {
        return field == functionalKeyBuilder.field1 ||
                field == functionalKeyBuilder.field2;
    }

    public int size() {
        return 2;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        if (value1 != NULL_VALUE) {
            functionalKeyBuilder.field1.acceptValue(functor, value1);
        }
        if (value2 != NULL_VALUE) {
            functionalKeyBuilder.field2.acceptValue(functor, value2);
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T visitor, CTX ctx) throws Exception {
        if (value1 != NULL_VALUE) {
            functionalKeyBuilder.field1.acceptValue(visitor, value1, ctx);
        }
        if (value2 != NULL_VALUE) {
            functionalKeyBuilder.field2.acceptValue(visitor, value2, ctx);
        }
        return visitor;
    }

        public <T extends Functor> T apply(T functor) throws Exception {
        if (value1 != NULL_VALUE) {
            functor.process(functionalKeyBuilder.field1, value1);
        }
        if (value2 != NULL_VALUE) {
            functor.process(functionalKeyBuilder.field2, value2);
        }
        return functor;
    }

    public FieldValue[] toArray() {
        if (value1 == NULL_VALUE && value2 == NULL_VALUE) {
            return EMPTY.toArray();
        }
        if (value1 != NULL_VALUE && value2 != NULL_VALUE) {
            return new FieldValue[]{FieldValue.value(functionalKeyBuilder.field1, value1),
                    FieldValue.value(functionalKeyBuilder.field2, value2)};
        }
        if (value1 != NULL_VALUE) {
            return new FieldValue[]{FieldValue.value(functionalKeyBuilder.field1, value1)};
        }
        return new FieldValue[]{FieldValue.value(functionalKeyBuilder.field2, value2)};
    }

    public FunctionalKeyBuilder getBuilder() {
        return functionalKeyBuilder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TwoFieldsMutableKey that) {
            if (functionalKeyBuilder.getType() != that.functionalKeyBuilder.getType()) {
                return false;
            }
            if (!Objects.equals(value1, that.value1)) {
                return false;
            }
            return Objects.equals(value2, that.value2);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = functionalKeyBuilder.getType().hashCode();
        result = 31 * result + (value1 != null ? value1.hashCode() : 0);
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        return result;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return field == functionalKeyBuilder.field1 ? value1 != NULL_VALUE : value2 != NULL_VALUE;
    }

    public String toString() {
        return "TwoFieldsMutableKey{" +
                "functionalKeyBuilder=" + functionalKeyBuilder +
                ", value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}
