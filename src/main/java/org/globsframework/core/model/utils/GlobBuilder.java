package org.globsframework.core.model.utils;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/*
Set the default value
 */
public class GlobBuilder implements FieldSetter<GlobBuilder>, FieldValues {
    private GlobType globType;
    private MutableGlob mutableGlob;

    public static GlobBuilder init(GlobType globType) {
        return new GlobBuilder(globType);
    }

    public static Glob create(GlobType type, FieldValue... values) {
        return init(type, values).get();
    }

    public static GlobBuilder init(GlobType type, FieldValues values) {
        GlobBuilder builder = new GlobBuilder(type);
        values.safeApply(forward(builder));
        return builder;
    }

    public static GlobBuilder init(GlobType type, FieldValue... values) {
        GlobBuilder builder = new GlobBuilder(type);
        FieldValues fieldValues = new ArrayFieldValues(values);
        fieldValues.safeApply(forward(builder));
        return builder;
    }

    public static GlobBuilder init(Key key, FieldValue... values) {
        GlobBuilder builder = new GlobBuilder(key.getGlobType());
        Functor functor = forward(builder);
        key.safeApplyOnKeyField(functor);
        FieldValues fieldValues = new ArrayFieldValues(values);
        fieldValues.safeApply(forward(builder));
        return builder;
    }

    private static Functor forward(GlobBuilder builder) {
        return new Functor() {
            public void process(Field field, Object value) throws Exception {
                builder.setValue(field, value);
            }
        };
    }

    public static GlobBuilder init(Key key, FieldValues values) {
        GlobBuilder builder = new GlobBuilder(key.getGlobType());
        key.safeApplyOnKeyField(forward(builder));
        values.safeApply(forward(builder));
        return builder;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        return mutableGlob.isSet(field);
    }

    public GlobBuilder set(DoubleField field, Double value) {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(DoubleField field, double value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(DoubleArrayField field, double[] value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(IntegerField field, Integer value) {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(IntegerField field, int value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(IntegerArrayField field, int[] value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(StringField field, String value) {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(StringArrayField field, String[] value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(BooleanField field, Boolean value) {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(LongField field, Long value) {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(LongArrayField field, long[] value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(LongField field, long value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(BlobField field, byte[] value) {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(DateField field, LocalDate value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(GlobField field, Glob value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(GlobArrayField field, Glob[] values) throws ItemNotFound {
        mutableGlob.set(field, values);
        return this;
    }

    public GlobBuilder set(GlobUnionField field, Glob value) throws ItemNotFound {
        mutableGlob.set(field, value);
        return this;
    }

    public GlobBuilder set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound {
        mutableGlob.set(field, values);
        return this;
    }

    public GlobBuilder setValue(Field field, Object value) throws ItemNotFound {
        mutableGlob.setValue(field, value);
        return this;
    }

    public GlobBuilder setObject(Field field, Object objectValue) {
        mutableGlob.setValue(field, objectValue);
        return this;
    }

    public Glob get() {
        try {
            return mutableGlob;
        } finally {
            mutableGlob = globType.instantiate();
        }
    }

    private GlobBuilder(GlobType globType) {
        mutableGlob = globType.instantiate();
        this.globType = globType;
        for (Field field : globType.getFields()) {
            mutableGlob.setValue(field, field.getDefaultValue());
        }
    }

    public void process(Field field, Object value) throws Exception {
        setObject(field, value);
    }

    public boolean isNull(Field field) throws ItemNotFound {
        return getValue(field) == null;
    }

    public Object getValue(Field field) throws ItemNotFound {
        return mutableGlob.getValue(field);
    }

    public Double get(DoubleField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public double get(DoubleField field, double valueIfNull) throws ItemNotFound {
        return mutableGlob.get(field, valueIfNull);
    }

    public double[] get(DoubleArrayField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public Integer get(IntegerField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public int get(IntegerField field, int valueIfNull) throws ItemNotFound {
        return mutableGlob.get(field, valueIfNull);
    }

    public int[] get(IntegerArrayField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public String get(StringField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public String[] get(StringArrayField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public Boolean get(BooleanField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public boolean get(BooleanField field, boolean defaultIfNull) {
        return mutableGlob.get(field, defaultIfNull);
    }

    public boolean[] get(BooleanArrayField field) {
        return mutableGlob.get(field);
    }

    public boolean isTrue(BooleanField field) throws ItemNotFound {
        return mutableGlob.isTrue(field);
    }

    public Long get(LongField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public long get(LongField field, long valueIfNull) throws ItemNotFound {
        return mutableGlob.get(field, valueIfNull);
    }

    public long[] get(LongArrayField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public BigDecimal get(BigDecimalField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public LocalDate get(DateField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public ZonedDateTime get(DateTimeField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public byte[] get(BlobField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public Glob get(GlobField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public Glob[] get(GlobArrayField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public Glob get(GlobUnionField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public Glob[] get(GlobArrayUnionField field) throws ItemNotFound {
        return mutableGlob.get(field);
    }

    public boolean contains(Field field) {
        return mutableGlob.contains(field);
    }

    public int size() {
        return mutableGlob.size();
    }

    public <T extends FieldValues.Functor> T apply(T functor) throws Exception {
        return mutableGlob.apply(functor);
    }

    public <T extends FieldValues.Functor> T safeApply(T functor) {
        return mutableGlob.safeApply(functor);
    }

    public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        return mutableGlob.accept(functor);
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        return mutableGlob.accept(functor, ctx);
    }

    public <T extends FieldValueVisitor> T safeAccept(T functor) {
        return mutableGlob.safeAccept(functor);
    }

    public FieldValue[] toArray() {
        return mutableGlob.toArray();
    }

    public FieldValues withoutKeyField() {
        return null;
    }

    public String toString() {
        return mutableGlob.toString();
    }
}
