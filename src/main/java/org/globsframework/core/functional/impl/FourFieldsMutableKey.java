package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.ItemNotFound;

/**
 * A 4-field functional key, unrolled: one object of 32 bytes where {@link ManyFieldsMutableKey} needs a second
 * allocation for its array, and a field found by comparing references rather than through an index table — which
 * also refuses a field of another type without a {@link FieldCheck}. Its {@link #hashCode} is the one
 * {@link ManyFieldsMutableKey} computes on the same fields, and has to stay so (globs-shared persists it).
 */
final class FourFieldsMutableKey extends AbstractFieldValue<MutableFunctionalKey>
        implements MutableFunctionalKey, FunctionalKey {
    private final FourFunctionalKeyBuilder functionalKeyBuilder;
    private Object value1;
    private Object value2;
    private Object value3;
    private Object value4;

    FourFieldsMutableKey(FourFunctionalKeyBuilder functionalKeyBuilder) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        this.value1 = NULL_VALUE;
        this.value2 = NULL_VALUE;
        this.value3 = NULL_VALUE;
        this.value4 = NULL_VALUE;
    }

    FourFieldsMutableKey(FourFunctionalKeyBuilder functionalKeyBuilder, Object value1, Object value2, Object value3, Object value4) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }

    protected MutableFunctionalKey doSet(Field field, Object o) {
        if (field == functionalKeyBuilder.field1) {
            value1 = o;
        } else if (field == functionalKeyBuilder.field2) {
            value2 = o;
        } else if (field == functionalKeyBuilder.field3) {
            value3 = o;
        } else if (field == functionalKeyBuilder.field4) {
            value4 = o;
        } else {
            throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
        }
        return this;
    }

    public MutableFunctionalKey setValue(Field field, Object o) {
        FieldCheck.checkValue(field, o);
        return doSet(field, o);
    }

    protected Object doGet(Field field) {
        if (field == functionalKeyBuilder.field1) return getOrNullValue(value1);
        if (field == functionalKeyBuilder.field2) return getOrNullValue(value2);
        if (field == functionalKeyBuilder.field3) return getOrNullValue(value3);
        if (field == functionalKeyBuilder.field4) return getOrNullValue(value4);
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public FunctionalKey getShared() {
        return this;
    }

    public FunctionalKey create() {
        return new FourFieldsMutableKey(functionalKeyBuilder, value1, value2, value3, value4);
    }

    public void unset(Field field) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            FieldCheck.check(field, functionalKeyBuilder.type);
        }
        if (field == functionalKeyBuilder.field1) {
            value1 = NULL_VALUE;
        } else if (field == functionalKeyBuilder.field2) {
            value2 = NULL_VALUE;
        } else if (field == functionalKeyBuilder.field3) {
            value3 = NULL_VALUE;
        } else if (field == functionalKeyBuilder.field4) {
            value4 = NULL_VALUE;
        } else {
            throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
        }
    }

    public boolean contains(Field field) {
        return field == functionalKeyBuilder.field1 ||
               field == functionalKeyBuilder.field2 ||
               field == functionalKeyBuilder.field3 ||
               field == functionalKeyBuilder.field4;
    }

    public int size() {
        return 4;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        if (value1 != NULL_VALUE) {
            functionalKeyBuilder.field1.acceptValue(functor, value1);
        }
        if (value2 != NULL_VALUE) {
            functionalKeyBuilder.field2.acceptValue(functor, value2);
        }
        if (value3 != NULL_VALUE) {
            functionalKeyBuilder.field3.acceptValue(functor, value3);
        }
        if (value4 != NULL_VALUE) {
            functionalKeyBuilder.field4.acceptValue(functor, value4);
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
        if (value3 != NULL_VALUE) {
            functionalKeyBuilder.field3.acceptValue(visitor, value3, ctx);
        }
        if (value4 != NULL_VALUE) {
            functionalKeyBuilder.field4.acceptValue(visitor, value4, ctx);
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
        if (value3 != NULL_VALUE) {
            functor.process(functionalKeyBuilder.field3, value3);
        }
        if (value4 != NULL_VALUE) {
            functor.process(functionalKeyBuilder.field4, value4);
        }
        return functor;
    }

    public FieldValue[] toArray() {
        int count = 0;
        if (value1 != NULL_VALUE) count++;
        if (value2 != NULL_VALUE) count++;
        if (value3 != NULL_VALUE) count++;
        if (value4 != NULL_VALUE) count++;
        final FieldValue[] result = new FieldValue[count];
        int i = 0;
        if (value1 != NULL_VALUE) {
            result[i++] = FieldValue.value(functionalKeyBuilder.field1, value1);
        }
        if (value2 != NULL_VALUE) {
            result[i++] = FieldValue.value(functionalKeyBuilder.field2, value2);
        }
        if (value3 != NULL_VALUE) {
            result[i++] = FieldValue.value(functionalKeyBuilder.field3, value3);
        }
        if (value4 != NULL_VALUE) {
            result[i++] = FieldValue.value(functionalKeyBuilder.field4, value4);
        }
        return result;
    }

    public FunctionalKeyBuilder getBuilder() {
        return functionalKeyBuilder;
    }

    private static boolean valueEqual(Field field, Object v1, Object v2) {
        return v1 == v2 || (v1 != NULL_VALUE && v2 != NULL_VALUE && field.valueEqual(v1, v2));
    }

    public boolean matches(FieldValues other) {
        final FourFunctionalKeyBuilder b = functionalKeyBuilder;
        return matches(b.field1, value1, other) && matches(b.field2, value2, other) &&
               matches(b.field3, value3, other) && matches(b.field4, value4, other);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof FourFieldsMutableKey that) {
            final FourFunctionalKeyBuilder b = functionalKeyBuilder;
            final FourFunctionalKeyBuilder tb = that.functionalKeyBuilder;
            if (b != tb && (b.field1 != tb.field1 || b.field2 != tb.field2 || b.field3 != tb.field3 || b.field4 != tb.field4)) {
                return false;
            }
            return valueEqual(b.field1, value1, that.value1) &&
                   valueEqual(b.field2, value2, that.value2) &&
                   valueEqual(b.field3, value3, that.value3) &&
                   valueEqual(b.field4, value4, that.value4);
        }
        return false;
    }

    public int hashCode() {
        final FourFunctionalKeyBuilder b = functionalKeyBuilder;
        int result = b.hash;
        result = 31 * result + valueHash(b.field1, value1);
        result = 31 * result + valueHash(b.field2, value2);
        result = 31 * result + valueHash(b.field3, value3);
        result = 31 * result + valueHash(b.field4, value4);
        return result;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        if (field == functionalKeyBuilder.field1) return value1 != NULL_VALUE;
        if (field == functionalKeyBuilder.field2) return value2 != NULL_VALUE;
        if (field == functionalKeyBuilder.field3) return value3 != NULL_VALUE;
        if (field == functionalKeyBuilder.field4) return value4 != NULL_VALUE;
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public String toString() {
        return functionalKeyBuilder.type.getName() + " : " +
               functionalKeyBuilder.field1.getName() + "=" + value1 + " ; " +
               functionalKeyBuilder.field2.getName() + "=" + value2 + " ; " +
               functionalKeyBuilder.field3.getName() + "=" + value3 + " ; " +
               functionalKeyBuilder.field4.getName() + "=" + value4;
    }
}
