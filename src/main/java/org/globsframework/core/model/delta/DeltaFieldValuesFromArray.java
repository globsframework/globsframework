package org.globsframework.core.model.delta;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.impl.AbstractFieldValues;
import org.globsframework.core.utils.Unset;
import org.globsframework.core.utils.exceptions.ItemNotFound;

class DeltaFieldValuesFromArray implements AbstractFieldValues {
    private GlobType type;
    private Object[] values;

    public DeltaFieldValuesFromArray(GlobType type, Object[] values) {
        this.type = type;
        this.values = values;
    }

    public Object getValue(Field field) throws ItemNotFound {
        return doCheckedGet(field);
    }

    public Object doCheckedGet(Field field) {
        Object value = values[field.getIndex()];
        if (value == Unset.VALUE) {
            throw new ItemNotFound(field.getName() + " not set.");
        }
        return value;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return values[field.getIndex()] != Unset.VALUE;
    }

    public boolean contains(Field field) {
        if (field.isKeyField()) {
            return false;
        }
        return values[field.getIndex()] != Unset.VALUE;
    }

    public int size() {
        int count = 0;
        for (Field field : type.getFields()) {
            if (!field.isKeyField() && values[field.getIndex()] != Unset.VALUE) {
                count++;
            }
        }
        return count;
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (Field field : type.getFields()) {
            Object value = values[field.getIndex()];
            if (value != Unset.VALUE && !field.isKeyField()) {
                field.acceptValue(functor, value);
            }
        }
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
      for (Field field : type.getFields()) {
          Object value = values[field.getIndex()];
          if (value != Unset.VALUE && !field.isKeyField()) {
              field.acceptValue(functor, value, ctx);
          }
      }
      return functor;
    }

    public <T extends FieldValues.Functor>
    T apply(T functor) throws Exception {
        for (Field field : type.getFields()) {
            Object value = values[field.getIndex()];
            if (value != Unset.VALUE && !field.isKeyField()) {
                functor.process(field, value);
            }
        }
        return functor;
    }

    public FieldValue[] toArray() {
        FieldValue[] fieldValues = new FieldValue[size()];
        int i = 0;
        for (Field field : type.getFields()) {
            Object value = values[field.getIndex()];
            if (value != Unset.VALUE && !field.isKeyField()) {
                fieldValues[i] = new FieldValue(field, value);
                i++;
            }
        }
        return fieldValues;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Field field : type.getFields()) {
            builder.append(field.getName()).append(":").append(values[field.getIndex()]).append(("\n"));
        }
        return builder.toString();
    }
}
