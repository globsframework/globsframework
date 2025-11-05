package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;

public class FieldValue {
    private final Field field;
    private final Object value;

    public static FieldValue value(Field field, Object value) {
        return new FieldValue(field, value);
    }

    public static FieldValue value(DoubleField field, Double value) {
        return new FieldValue(field, value);
    }

    public static FieldValue value(IntegerField field, Integer value) {
        return new FieldValue(field, value);
    }

    public static FieldValue value(StringField field, String value) {
        return new FieldValue(field, value);
    }

    public static FieldValue value(BooleanField field, Boolean value) {
        return new FieldValue(field, value);
    }

    public static FieldValue value(LongField field, Long value) {
        return new FieldValue(field, value);
    }

    public static FieldValue value(BytesField field, byte[] value) {
        return new FieldValue(field, value);
    }

    public FieldValue(Field field, Object value) {
        this.field = field;
        this.value = field.normalize(value);
    }

    public Field getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldValue that = (FieldValue) o;

        if (!field.equals(that.field)) {
            return false;
        }
        if (value != null ? !field.valueEqual(value, that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = field.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return field.getName() + ":" + value;
    }
}
