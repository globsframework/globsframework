package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.utils.FieldCheck;
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
        } else if (functionalKeyBuilder.field2 == field) {
            value2 = o;
        } else {
            throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
        }
        return this;
    }

    protected Object doGet(Field field) {
        if (field == functionalKeyBuilder.field1) return getNotNullValue(value1);
        if (field == functionalKeyBuilder.field2) return getNotNullValue(value2);
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public FunctionalKey getShared() {
        return this;
    }

    public FunctionalKey create() {
        return new TwoFieldsMutableKey(functionalKeyBuilder, value1, value2);
    }

    public void unset(Field field) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            FieldCheck.check(field, functionalKeyBuilder.getType());
        }
        if (field == functionalKeyBuilder.field1) {
            value1 = NULL_VALUE;
        }
        else if (field == functionalKeyBuilder.field2) {
            value2 = NULL_VALUE;
        }
        else {
            throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
        }
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
            if (functionalKeyBuilder.field1 != that.functionalKeyBuilder.field1 ||
                functionalKeyBuilder.field2 != that.functionalKeyBuilder.field2) {
                return false;
            }
            if (value1 == NULL_VALUE || that.value1 == NULL_VALUE) {
                if (value1 != that.value1) {
                    return false;
                }
            } else {
                if (!functionalKeyBuilder.field1.valueEqual(value1, that.value1)) {
                    return false;
                }
            }
            if (value2 == NULL_VALUE || that.value2 == NULL_VALUE) {
                if (value2 != that.value2) {
                    return false;
                }
            }else {
                if (!functionalKeyBuilder.field2.valueEqual(value2, that.value2)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + (value1 != null ? value1.hashCode() : 0);
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        return result;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        if (field == functionalKeyBuilder.field1) return value1 != NULL_VALUE;
        if (field == functionalKeyBuilder.field2) return value2 != NULL_VALUE;
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public String toString() {
        return functionalKeyBuilder.field1.getName() + "=" + value1 + "/" + functionalKeyBuilder.field2.getName() + "=" + value2;
    }
}
