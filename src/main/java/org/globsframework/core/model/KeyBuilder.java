package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.impl.*;
import org.globsframework.core.model.utils.DefaultFieldValues;
import org.globsframework.core.model.utils.FieldValueGetter;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;
import org.globsframework.core.utils.exceptions.MissingInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class KeyBuilder implements FieldSetter<KeyBuilder> {
    private GlobType globType;
    private MutableKey settable;

    private KeyBuilder(GlobType type) {
        Field[] keyFields = type.getKeyFields();
        int len = keyFields.length;
        if (len == 1) {
            settable = new SingleFieldKey(keyFields[0]);
        } else if (len == 2) {
            settable = new TwoFieldKey(type);
        } else if (len == 3) {
            settable = new ThreeFieldKey(type);
        } else if (len == 4) {
            settable = new FourFieldKey(type);
        } else {
            settable = new CompositeKey(type);
        }
        this.globType = type;
    }

    public static KeyBuilder init(GlobType type) {
        return new KeyBuilder(type);
    }

    public static Key newEmptyKey(GlobType type) {
        return new EmptyKey(type);
    }

    public static KeyBuilder init(Field field, Object value) {
        KeyBuilder keyBuilder = new KeyBuilder(field.getGlobType());
        keyBuilder.setObject(field, value);
        return keyBuilder;
    }

    public static KeyBuilder init(Field field, int value) {
        KeyBuilder keyBuilder = new KeyBuilder(field.getGlobType());
        keyBuilder.setObject(field, value);
        return keyBuilder;
    }

    public static Key newKey(GlobType type, Object value) throws InvalidParameter {
        return newKey(SingleFieldKey.getKeyField(type), value);
    }

    public static Key newKey(Field field, Object value) throws InvalidParameter {
        if (field instanceof LongField) {
            return new LongKeyField(field, (Long) value);
        }
        return new SingleFieldKey(field, value);
    }

    public static Key newKey(Field field1, Object value1, Field field2, Object value2) throws InvalidParameter {
        return new TwoFieldKey(field1, value1, field2, value2);
    }

    public static Key createFromValues(GlobType type, final Map<Field, Object> values) throws MissingInfo {
        Field[] keyFields = type.getKeyFields();
        if (keyFields.length == 1) {
            Field field = keyFields[0];
            return createSingle(type, field, values.containsKey(field), values.get(field));
        }
        if (keyFields.length == 2) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            return new TwoFieldKey(field1, values.get(field1), field2, values.get(field2));
        }
        if (keyFields.length == 3) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            Field field3 = keyFields[2];
            return new ThreeFieldKey(field1, values.get(field1),
                    field2, values.get(field2),
                    field3, values.get(field3));
        }
        if (keyFields.length == 4) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            Field field3 = keyFields[2];
            Field field4 = keyFields[3];
            return new FourFieldKey(field1, values.get(field1),
                    field2, values.get(field2),
                    field3, values.get(field3),
                    field4, values.get(field4));
        }
        return createKey(type, new FieldValueGetter() {
            public boolean contains(Field field) {
                return values.containsKey(field);
            }

            public Object get(Field field) {
                return values.get(field);
            }
        });
    }

    public static Key createFromValues(GlobType type, final FieldValues values) {
        Field[] keyFields = type.getKeyFields();
        if (keyFields.length == 1) {
            Field field = keyFields[0];
            return createSingle(type, field, values.contains(field), values.getValue(field));
        }
        if (keyFields.length == 2) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            return new TwoFieldKey(field1, values.getValue(field1), field2, values.getValue(field2));
        }
        if (keyFields.length == 3) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            Field field3 = keyFields[2];
            return new ThreeFieldKey(field1, values.getValue(field1),
                    field2, values.getValue(field2),
                    field3, values.getValue(field3));
        }
        if (keyFields.length == 4) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            Field field3 = keyFields[2];
            Field field4 = keyFields[3];
            return new FourFieldKey(field1, values.getValue(field1),
                    field2, values.getValue(field2),
                    field3, values.getValue(field3),
                    field4, values.getValue(field4));
        }

        return createKey(type, new FieldValueGetter() {
            public boolean contains(Field field) {
                return values.contains(field);
            }

            public Object get(Field field) {
                return values.getValue(field);
            }
        });
    }

    public static Key createFromValues(GlobType type, FieldValue... values) {
        DefaultFieldValues defaultFieldValues = new DefaultFieldValues();
        for (FieldValue value : values) {
            defaultFieldValues.setValue(value.getField(), value.getValue());
        }
        return createFromValues(type, defaultFieldValues);
    }

    public static Key createFromValues(GlobType type, final Object[] values) {

        if (values.length != type.getFieldCount()) {
            throw new InvalidParameter("Array should have " + type.getFieldCount() + " elements for type: " +
                    type.getName() + " - array content: " + Arrays.toString(values));
        }

        Field[] keyFields = type.getKeyFields();
        if (keyFields.length == 1) {
            Field field = keyFields[0];
            Object value = values[field.getIndex()];
            return createSingle(type, field, value != null, value);
        }
        if (keyFields.length == 2) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            return new TwoFieldKey(field1, values[field1.getIndex()], field2, values[field2.getIndex()]);
        }
        if (keyFields.length == 3) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            Field field3 = keyFields[2];
            return new ThreeFieldKey(field1, values[field1.getIndex()],
                    field2, values[field2.getIndex()],
                    field3, values[field3.getIndex()]);
        }
        if (keyFields.length == 4) {
            Field field1 = keyFields[0];
            Field field2 = keyFields[1];
            Field field3 = keyFields[2];
            Field field4 = keyFields[3];
            return new FourFieldKey(field1, values[field1.getIndex()],
                    field2, values[field2.getIndex()],
                    field3, values[field3.getIndex()],
                    field4, values[field4.getIndex()]);
        }
        return createKey(type, new FieldValueGetter() {
            public boolean contains(Field field) {
                return values[field.getIndex()] != null;
            }

            public Object get(Field field) {
                return values[field.getIndex()];
            }
        });
    }

    private static Key createSingle(GlobType type, Field field, boolean present, Object value)
            throws MissingInfo {
        if (!present) {
            throw new MissingInfo("Field '" + field.getName() +
                    "' missing for identifying a Glob of type: " + type.getName());
        }
        if (field instanceof LongField) {
            return new LongKeyField(field, (Long) value);
        }
        return new SingleFieldKey(field, value);
    }

    private static Key createKey(GlobType type, FieldValueGetter getter) throws MissingInfo {
        return new CompositeKey(type, getter);
    }

    public static Key create(GlobType type, Object singleFieldValue) {
        return newKey(type, singleFieldValue);
    }

    public static Key create(Field field1, Object value1, Field field2, Object value2) {
        return new TwoFieldKey(field1, value1, field2, value2);
    }

    public static Key newKey(Field field1, Object value1, Field field2, Object value2, Field field3, Object value3) {
        return new ThreeFieldKey(field1, value1, field2, value2, field3, value3);
    }

    public static Key create(Field field1, Object value1, Field field2, Object value2,
                             Field field3, Object value3, Field field4, Object value4) {
        return new FourFieldKey(field1, value1, field2, value2, field3, value3, field4, value4);
    }

    public static KeyBuilder create(GlobType type) {
        return init(type);
    }

    public KeyBuilder setObject(Field field, Object value) throws InvalidParameter {
        settable.setValue(field, value);
        return this;
    }

    public Key get() {
        return settable.duplicateKey();
    }

    public Key getShared() {
        return settable;
    }

    public KeyBuilder reuse() {
        settable.reset();
        return this;
    }

    public KeyBuilder set(DoubleField field, Double value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(DateField field, Date value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(IntegerField field, Integer value) throws ItemNotFound {
        return setObject(field, value);
    }


    public KeyBuilder set(StringField field, String value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(BooleanField field, Boolean value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(LongField field, Long value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(LongField field, long value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(BytesField field, byte[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(DoubleArrayField field, double[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder setValue(Field field, Object value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(DoubleField field, double value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(IntegerField field, int value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(IntegerArrayField field, int[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(StringArrayField field, String[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(LongArrayField field, long[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(DateField field, LocalDate value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(GlobField field, Glob value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(GlobArrayField field, Glob[] values) throws ItemNotFound {
        return setObject(field, values);
    }

    public KeyBuilder set(GlobUnionField field, Glob value) throws ItemNotFound {
        return setObject(field, value);
    }

    public KeyBuilder set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound {
        return setObject(field, values);
    }

    public GlobType getGlobType() {
        return globType;
    }
}
