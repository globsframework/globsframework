package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.FieldsValueWithPreviousScanner;
import org.globsframework.core.model.MutableFieldValues;
import org.globsframework.core.model.impl.AbstractFieldValuesWithPrevious;
import org.globsframework.core.utils.Unset;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public class DefaultFieldValuesWithPrevious extends AbstractFieldValuesWithPrevious {
    private GlobType type;
    private Object[] values;
    private Object[] previousValues;

    public DefaultFieldValuesWithPrevious(FieldsValueWithPreviousScanner fieldsValueWithPreviousScanner) {
        fieldsValueWithPreviousScanner.safeApplyWithPrevious(new FunctorWithPrevious() {
            public void process(Field field, Object value, Object previous) throws Exception {
                if (type == null) {
                    type = field.getGlobType();
                    values = new Object[type.getFieldCount()];
                    previousValues = new Object[type.getFieldCount()];
                    for (Field f : type.getFields()) {
                        int index = f.getIndex();
                        values[index] = Unset.VALUE;
                        previousValues[index] = Unset.VALUE;
                    }
                }
                setValue(field, value, previous);
            }
        });
    }

    public DefaultFieldValuesWithPrevious(GlobType type) {
        this.type = type;
        this.values = new Object[type.getFieldCount()];
        this.previousValues = new Object[type.getFieldCount()];
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            values[index] = Unset.VALUE;
            previousValues[index] = Unset.VALUE;
        }
    }

    public MutableFieldValues getNewValues() {
        return new GlobArrayFieldValues(type, values);
    }

    public MutableFieldValues getPreviousValues() {
        return new GlobArrayFieldValues(type, previousValues);
    }

    public void setValue(Field field, Object value, Object previousValue) {
        int index = field.getIndex();
        values[index] = value;
        previousValues[index] = previousValue;
    }

    public void setValue(Field field, Object value) {
        values[field.getIndex()] = value;
    }

    public void setPreviousValue(Field field, Object previousValue) {
        previousValues[field.getIndex()] = previousValue;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return values[field.getIndex()] != Unset.VALUE;
    }

    public boolean contains(Field field) {
        return values[field.getIndex()] != Unset.VALUE;
    }

    public int size() {
        int count = 0;
        for (Object value : values) {
            if (value != Unset.VALUE) {
                count++;
            }
        }
        return count;
    }

    public <T extends FieldValueVisitor> T acceptOnPrevious(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (previousValues[index] != Unset.VALUE) {
                field.acceptValue(functor, previousValues[index]);
            }
        }
        return functor;
    }

    public <T extends FunctorWithPrevious> T applyWithPrevious(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (values[index] != Unset.VALUE) {
                functor.process(field, values[index], previousValues[index]);
            }
        }
        return functor;
    }

    public <T extends FieldValues.Functor> T applyOnPrevious(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (previousValues[index] != Unset.VALUE) {
                functor.process(field, previousValues[index]);
            }
        }
        return functor;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (values[index] != Unset.VALUE) {
                field.acceptValue(functor, values[index]);
            }
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (values[index] != Unset.VALUE) {
                field.acceptValue(functor, values[index], ctx);
            }
        }
        return functor;
    }

    public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (values[index] != Unset.VALUE) {
                functor.process(field, values[index]);
            }
        }
        return functor;
    }

    public FieldValue[] toArray() {
        FieldValue[] result = new FieldValue[size()];
        int resultIndex = 0;
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (values[index] != Unset.VALUE) { // fix via relecture
                result[resultIndex++] = new FieldValue(field, values[index]);
            }
        }
        return result;
    }

    protected Object doGet(Field field) {
        if (!field.getGlobType().equals(type)) {
            throw new ItemNotFound("Field '" + field.getName() + "' is declared for type '" +
                    field.getGlobType().getName() + "' and not for '" + type.getName() + "'");
        }
        return values[field.getIndex()];
    }

    protected Object doGetPrevious(Field field) {
        return previousValues[field.getIndex()];
    }

    public void completeForCreate() {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (values[index] == Unset.VALUE) {
                values[index] = null;
            }
            if (previousValues[index] == Unset.VALUE) {
                previousValues[index] = null;
            }
        }
    }

    public void completeForUpdate() {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if ((values[index] != Unset.VALUE) && (previousValues[index] == Unset.VALUE)) {
                previousValues[index] = field.getDefaultValue();
            }
        }
    }

    public void completeForDelete() {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (!field.isKeyField()) {
                values[index] = null;
                if (previousValues[index] == Unset.VALUE) {
                    previousValues[index] = null;
                }
            }
        }
    }
}
