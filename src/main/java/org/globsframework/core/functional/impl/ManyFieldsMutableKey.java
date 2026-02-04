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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManyFieldsMutableKey extends AbstractFieldValue<MutableFunctionalKey>
        implements MutableFunctionalKey, FunctionalKey {
    private final ManyFunctionalKeyBuilder functionalKeyBuilder;
    private final int[] index;
    private final Object[] values;

    ManyFieldsMutableKey(ManyFunctionalKeyBuilder functionalKeyBuilder) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        values = new Object[functionalKeyBuilder.fields.length];
        Arrays.fill(values, NULL_VALUE);
        index = functionalKeyBuilder.index;
    }

    ManyFieldsMutableKey(ManyFunctionalKeyBuilder functionalKeyBuilder, FieldValues fieldValues) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        Field[] fields = functionalKeyBuilder.fields;
        index = functionalKeyBuilder.index;
        values = new Object[fields.length];
        int i = 0;
        for (Field field : fields) {
            values[i] = fieldValues.isSet(field) ? fieldValues.getValue(field) : NULL_VALUE;
            i++;
        }
    }

    private ManyFieldsMutableKey(ManyFunctionalKeyBuilder functionalKeyBuilder, Object[] values) {
        this.functionalKeyBuilder = functionalKeyBuilder;
        this.values = values;
        index = functionalKeyBuilder.index;
    }

    protected MutableFunctionalKey doSet(Field field, Object o) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            FieldCheck.check(field, functionalKeyBuilder.getType());
        }

        final int index = this.index[field.getIndex()];
        if (index >= 0) {
            values[index] = o;
            return this;
        }
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    protected Object doGet(Field field) {
        final int index = this.index[field.getIndex()];
        if (index >= 0) {
            return getNotNullValue(values[index]);
        }
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public FunctionalKey getShared() {
        return this;
    }

    public FunctionalKey create() {
        return new ManyFieldsMutableKey(functionalKeyBuilder, Arrays.copyOf(values, values.length));
    }

    public void unset(Field field) {
        final int index = this.index[field.getIndex()];
        if (index >= 0) {
            values[index] = NULL_VALUE;
            return;
        }
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public boolean contains(Field field) {
        return this.index[field.getIndex()] >= 0;
    }

    public int size() {
        return values.length;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        Field[] fields = functionalKeyBuilder.fields;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != NULL_VALUE) {
                fields[i].acceptValue(functor, values[i]);
            }
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        Field[] fields = functionalKeyBuilder.fields;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != NULL_VALUE) {
                fields[i].safeAcceptValue(functor, values[i], ctx);
            }
        }
        return functor;
    }

    public <T extends Functor> T apply(T functor) throws Exception {
        Field[] fields = functionalKeyBuilder.fields;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != NULL_VALUE) {
                functor.process(fields[i], values[i]);
            }
        }
        return functor;
    }

    public FieldValue[] toArray() {
        List<FieldValue> fieldValueList = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != NULL_VALUE) {
                fieldValueList.add(FieldValue.value(functionalKeyBuilder.fields[i], values[i]));
            }
        }
        return fieldValueList.toArray(FieldValue[]::new);
    }

    public FunctionalKeyBuilder getBuilder() {
        return functionalKeyBuilder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ManyFieldsMutableKey that) {
            final Field[] f1 = that.functionalKeyBuilder.fields;
            final Field[] f2 = functionalKeyBuilder.fields;
            for (int i = 0; i < values.length; i++) {
                if (f1[i] != f2[i]) {
                    return false;
                }
                if (values[i] == NULL_VALUE || that.values[i] == NULL_VALUE) {
                    if (values[i] != that.values[i]) {
                        return false;
                    }
                } else if (!f1[i].valueEqual(values[i], that.values[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 1; // make hash stable => GlobType is not part of the hash
        for (Object value : values) {
            result = 31 * result + (value == null ? 0 : value.hashCode());
        }
        return result;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        final int index = this.index[field.getIndex()];
        if (index >= 0) {
            return values[index] != NULL_VALUE;
        }
        throw new ItemNotFound("Field " + field.getName() + " not part of the functional key");
    }

    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Field[] fields = functionalKeyBuilder.getFields();
        for (int i = 0; i < values.length; i++) {
            stringBuilder.append(fields[i].getName()).append("=").append(values[i]).append("/");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }
}
