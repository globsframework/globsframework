package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.FieldsValueScanner;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultFieldValues extends AbstractMutableFieldValues {
    private Map<Field, Object> values = new HashMap<Field, Object>();

    public DefaultFieldValues() {
    }

    public DefaultFieldValues(FieldsValueScanner newValues) {
        newValues.safeApply(new Functor() {
            public void process(Field field, Object value) throws Exception {
                values.put(field, value);
            }
        });
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return values.containsKey(field);
    }

    public boolean contains(Field field) {
        return values.containsKey(field);
    }

    public Object doCheckedGet(Field field) {
        return values.get(field);
    }

    public int size() {
        return values.size();
    }

    public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            functor.process(entry.getKey(), entry.getValue());
        }
        return functor;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            entry.getKey().acceptValue(functor, entry.getValue());
        }
        return functor;
    }
    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            entry.getKey().acceptValue(functor, entry.getValue(), ctx);
        }
        return functor;
    }

    public DefaultFieldValues setValue(Field field, Object value) throws InvalidParameter {
        field.checkValue(value);
        values.put(field, value);
        return this;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (Iterator<Map.Entry<Field, Object>> iterator = values.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Field, Object> entry = iterator.next();
            Field field = entry.getKey();
            builder.append(field.getName());
            builder.append('=');
            builder.append(getValue(field));
            if (iterator.hasNext()) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public void remove(Field field) {
        values.remove(field);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DefaultFieldValues that = (DefaultFieldValues) o;

        if (values != null ? !values.equals(that.values) : that.values != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (values != null ? values.hashCode() : 0);
    }

    public FieldValue[] toArray() {
        FieldValue[] array = new FieldValue[values.size()];
        int index = 0;
        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            array[index] = new FieldValue(entry.getKey(), entry.getValue());
            index++;
        }
        return array;
    }

}
