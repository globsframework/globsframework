package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.utils.DefaultFieldValues;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

public class FieldValuesBuilder {
    private DefaultFieldValues values = new DefaultFieldValues();

    public static FieldValuesBuilder init() {
        return new FieldValuesBuilder();
    }

    public static FieldValuesBuilder init(Field field, Object value) {
        FieldValuesBuilder builder = new FieldValuesBuilder();
        return builder.setValue(field, value);
    }

    public static FieldValuesBuilder init(Map<Field, Object> map) {
        FieldValuesBuilder builder = new FieldValuesBuilder();
        builder.set(map);
        return builder;
    }

    public static FieldValuesBuilder init(FieldValues values) {
        FieldValuesBuilder builder = new FieldValuesBuilder();
        for (FieldValue value : values.toArray()) {
            builder.setValue(value.getField(), value.getValue());
        }
        return builder;
    }

    public static FieldValuesBuilder initWithoutKeyFields(FieldValues values) {
        return init(removeKeyFields(values));
    }

    public static FieldValuesBuilder init(FieldValue... values) {
        FieldValuesBuilder builder = new FieldValuesBuilder();
        for (FieldValue value : values) {
            builder.setValue(value.getField(), value.getValue());
        }
        return builder;
    }

    public FieldValuesBuilder set(Map<Field, Object> map) {
        for (Map.Entry<Field, Object> entry : map.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public static FieldValues createEmpty(GlobType type, boolean includeKeyFields) {
        DefaultFieldValues values = new DefaultFieldValues();
        for (Field field : type.getFields()) {
            if (!field.isKeyField() || includeKeyFields) {
                values.setValue(field, null);
            }
        }
        return values;
    }

    public FieldValuesBuilder set(FieldValues values) {
        for (FieldValue fieldValue : values.toArray()) {
            setValue(fieldValue.getField(), fieldValue.getValue());
        }
        return this;
    }

    public FieldValuesBuilder set(BooleanField field, Boolean value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(StringField field, String value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(StringArrayField field, String[] value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(IntegerField field, Integer value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(IntegerArrayField field, int[] values) {
        return setValue(field, values);
    }

    public FieldValuesBuilder set(LongArrayField field, long[] values) {
        return setValue(field, values);
    }

    public FieldValuesBuilder set(DoubleArrayField field, double[] values) {
        return setValue(field, values);
    }


    public void add(IntegerField field, Integer value) {
        if (value == null) {
            return;
        }
        Integer previous = values.get(field);
        if (previous == null) {
            previous = 0;
        }
        set(field, value + previous);
    }

    public FieldValuesBuilder set(DoubleField field, Double value) {
        return setValue(field, value);
    }

    public void add(DoubleField field, Double value) {
        if (value == null) {
            return;
        }
        Double previous = values.get(field);
        if (previous == null) {
            previous = 0.00;
        }
        set(field, value + previous);
    }


    public FieldValuesBuilder set(BytesField field, byte[] value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(LongField field, Long value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(BigDecimalField field, BigDecimal decimal) {
        return setValue(field, decimal);
    }

    public FieldValuesBuilder set(BigDecimalArrayField field, BigDecimal[] decimals) {
        return setValue(field, decimals);
    }

    public FieldValuesBuilder set(DateField field, LocalDate date) {
        return setValue(field, date);
    }

    public FieldValuesBuilder set(DateTimeField field, ZonedDateTime date) {
        return setValue(field, date);
    }

    public FieldValuesBuilder set(GlobField field, Glob value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(GlobArrayField field, Glob[] values) {
        return setValue(field, values);
    }

    public FieldValuesBuilder set(GlobUnionField field, Glob value) {
        return setValue(field, value);
    }

    public FieldValuesBuilder set(GlobArrayUnionField field, Glob[] values) {
        return setValue(field, values);
    }

    public FieldValuesBuilder setValue(Field field, Object value) {
        values.setValue(field, value);
        return this;
    }

    public FieldValuesBuilder set(BooleanArrayField field, boolean[] value) {
        values.setValue(field, value);
        return this;
    }


    public FieldValuesBuilder set(FieldValue... values) {
        for (FieldValue value : values) {
            this.values.setValue(value.getField(), value.getValue());
        }
        return this;
    }

    public FieldValuesBuilder remove(Field field) {
        values.remove(field);
        return this;
    }

    public MutableFieldValues get() {
        return values;
    }

    public boolean contains(Field field) {
        return values.contains(field);
    }

    public FieldValue[] toArray() {
        return values.toArray();
    }

    public static FieldValues removeKeyFields(FieldValues input) {
        final FieldValuesBuilder builder = new FieldValuesBuilder();
        input.safeApply(new FieldValues.Functor() {
            public void process(Field field, Object value) throws Exception {
                if (!field.isKeyField()) {
                    builder.setValue(field, value);
                }
            }
        });
        return builder.get();
    }

    public int size() {
        return values.size();
    }

    public String toString() {
        return values.toString();
    }
}
