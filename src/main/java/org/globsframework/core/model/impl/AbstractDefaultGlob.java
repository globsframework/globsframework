package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.model.*;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractDefaultGlob implements MutableGlob, FieldValues, Key {
    protected final GlobType type;
    protected final Object[] values;
    protected int hashCode;
    private int reserve = 0;

    protected AbstractDefaultGlob(GlobType type) {
        this(type, new Object[type.getFieldCount()]);
    }

    public AbstractDefaultGlob(GlobType type, Object[] values) {
        this.type = type;
        this.values = values;
    }

    final public GlobType getType() {
        return type;
    }

    final public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        for (Field field : type.getFields()) {
            final int index = field.getIndex();
            if (isSetAt(index)) { //  || field.isKeyField()
                field.acceptValue(functor, values[index]);
            }
        }
        return functor;
    }

    final public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
        for (Field field : type.getFields()) {
            final int index = field.getIndex();
            if (isSetAt(index)) {
                field.acceptValue(functor, values[index], ctx);
            }
        }
        return functor;
    }

    final public <T extends FieldValues.Functor>
    T apply(T functor) throws Exception {
        for (Field field : type.getFields()) {
            int index = field.getIndex();
            if (isSetAt(index)) {  //  || field.isKeyField()
                functor.process(field, values[index]);
            }
        }
        return functor;
    }

    final public Object uncheckGet(Field field) {
        return values[field.getIndex()];
    }

    final public Object get(int index) {
        return values[index];
    }

    final public boolean isNull(int index) {
        return values[index] == null;
    }

    final public void set(int index, Object value) {
        values[index] = value;
        setSetAt(index);
    }

    final public MutableGlob uncheckedSet(Field field, Object value) {
        set(field.getIndex(), value);
        return this;
    }

    final public boolean isSet(Field field) throws ItemNotFound {
        return isSetAt(field.getIndex());
    }

    abstract public void setSetAt(int index);

    abstract public boolean isSetAt(int index);

    abstract public void clearSetAt(int index);

    final public MutableGlob unset(Field field) {
        int index = field.getIndex();
        values[index] = null;
        clearSetAt(index);
        return this;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        toString(buffer);
        return buffer.toString();
    }

    final public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!Key.class.isAssignableFrom(o.getClass())) {
            return false;
        }

        Key otherKey = (Key) o;
        if (!Glob.class.isAssignableFrom(o.getClass())) {
            return otherKey.equals(this);
        }

        if (type != otherKey.getGlobType()) {
            return false;
        }

        Field[] keyFields = type.getKeyFields();
        for (Field field : keyFields) {
            if (!field.valueEqual(getValue(field), otherKey.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    @Override
    final public MutableGlob getMutable(GlobField field) throws ItemNotFound {
        return getMutableGlob(get(field), field);
    }

    @Override
    final public MutableGlob[] getMutable(GlobArrayField field) throws ItemNotFound {
        return getMutableGlobs(get(field), field);
    }

    @Override
    final public MutableGlob getMutable(GlobUnionField field) throws ItemNotFound {
        return getMutableGlob(get(field), field);
    }

    private MutableGlob getMutableGlob(Glob glob, Field field) {
        if (glob == null || glob instanceof MutableGlob) {
            return (MutableGlob) glob;
        }
        throw new ClassCastException(glob.getClass().getName() + " is not mutable on field " + field.getName());
    }

    @Override
    final public MutableGlob[] getMutable(GlobArrayUnionField field) throws ItemNotFound {
        return getMutableGlobs(get(field), field);
    }

    private MutableGlob[] getMutableGlobs(Glob[] globs, Field field) {
        if (globs != null) {
            if (globs instanceof MutableGlob[]) {
                return (MutableGlob[]) globs;
            } else {
                if (FieldCheck.CheckGlob.shouldCheck) {
                    for (Glob value : globs) {
                        if (value != null && !(value instanceof MutableGlob)) {
                            throw new ClassCastException(value.getClass().getName() + " is not mutable on field " + field.getName());
                        }
                    }
                }
                final MutableGlob[] value = Arrays.copyOfRange(globs, 0, globs.length, MutableGlob[].class);
                setObject(field, value);
                return value;
            }
        } else {
            return null;
        }
    }

    final public MutableGlob set(IntegerField field, Integer value) {
        return setObject(field, value);
    }

    final public MutableGlob set(IntegerField field, int value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(IntegerArrayField field, int[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(DoubleField field, Double value) {
        return setObject(field, value);
    }

    final public MutableGlob set(DoubleField field, double value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(DoubleArrayField field, double[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(LongField field, Long value) {
        return setObject(field, value);
    }

    final public MutableGlob set(LongField field, long value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(LongArrayField field, long[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(StringField field, String value) {
        return setObject(field, value);
    }

    final public MutableGlob set(StringArrayField field, String[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(BigDecimalField field, BigDecimal value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(BigDecimalArrayField field, BigDecimal[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(BooleanField field, Boolean value) {
        return setObject(field, value);
    }

    final public MutableGlob set(BytesField field, byte[] value) {
        return setObject(field, value);
    }

    final public MutableGlob setValue(Field field, Object value) {
        return setObject(field, value);
    }

    final public MutableGlob set(BooleanArrayField field, boolean[] value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(DateField field, LocalDate value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(DateTimeField field, ZonedDateTime value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(GlobField field, Glob value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(GlobArrayField field, Glob[] values) throws ItemNotFound {
        return setObject(field, values);
    }

    final public MutableGlob set(GlobUnionField field, Glob value) throws ItemNotFound {
        return setObject(field, value);
    }

    final public MutableGlob set(GlobArrayUnionField field, Glob[] values) throws ItemNotFound {
        return setObject(field, values);
    }

    final public MutableGlob setValues(FieldValues values) {
        values.safeApply(this::setObject);
        return this;
    }

    final public Key getTargetKey(Link link) {
        if (!link.getSourceType().equals(getType())) {
            throw new InvalidParameter("Link '" + link + " cannot be used with " + this);
        }

        KeyBuilder keyBuilder = KeyBuilder.init(link.getTargetType());
        link.apply((sourceField, targetField) -> {
            Object value = getValue(sourceField);
            keyBuilder.setObject(targetField, value);

        });
        return keyBuilder.get();
    }

    final public FieldValues getValues() {
        FieldValuesBuilder builder = FieldValuesBuilder.init();
        for (Field field : getType().getFields()) {
            if (!field.isKeyField()) {
                builder.setValue(field, uncheckGet(field));
            }
        }
        return builder.get();
    }

    final public FieldValue[] toArray() {
        List<FieldValue> fieldValueList = new ArrayList<>();
        for (Field field : getType().getFields()) {
            if (isSet(field)) {
                fieldValueList.add(new FieldValue(field, uncheckGet(field)));
            }
        }
        return fieldValueList.toArray(FieldValue[]::new);
    }

    final public Object doCheckedGet(Field field) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            FieldCheck.check(field, getType());
            checkReserved();
        }
        return uncheckGet(field);
    }

    final public MutableGlob setObject(Field field, Object value) {
        if (FieldCheck.CheckGlob.shouldCheck) {
            if (isHashComputed() && field.isKeyField()) {
                throw new RuntimeException(field.getFullName() + " is a key value and the hashCode is already computed.");
            }
            FieldCheck.check(field, getType(), value);
            checkReserved();
        }
        return uncheckedSet(field, value);
    }

    final public Key getKey() {
        return this;
    }

    final public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }
        return computeHash();
    }

    private int computeHash() {
        int hashCode = getType().hashCode();
        for (Field keyField : getType().getKeyFields()) {
            Object value = getValue(keyField);
            hashCode = 31 * hashCode + (value != null ? keyField.valueHash(value) : 0);
        }
        if (hashCode == 0) {
            hashCode = 31;
        }
        this.hashCode = hashCode;
        return hashCode;
    }

    final public boolean isHashComputed() {
        return hashCode != 0;
    }

    public void checkReserved() {
        if (reserve < 0) {
            throw new ReservationException("Data not reserved");
        }
    }

    @Override
    public void reserve(int key) {
        if (key <= 0) {
            throw new ReservationException("Reserved key <= 0 Got " + key);
        }
        if (reserve > 0) {
            throw new ReservationException("Already reserved by " + key);
        }
        reserve = key;
    }

    @Override
    public boolean release(int key) {
        if (key <= 0) {
            throw new ReservationException("Released key <= 0 Got " + key);
        }
        if (reserve == -key) {
            return false;
        }
        if (reserve != 0) {
            if (reserve != key) {
                throw new ReservationException("Can not release data : reserved by " + reserve + " != " + key);
            }
            reserve = -key;
        } else {
            throw new ReservationException("Can not release not own Glob '" + key + "'");
        }
        hashCode = 0;
        resetSet();
        return true;
    }

    @Override
    public void unReserve() {
        hashCode = 0;
        reserve = 0;
        resetSet();
    }

    abstract void resetSet();

    @Override
    public boolean isReserved() {
        return reserve > 0;
    }

    @Override
    public boolean isReservedBy(int key) {
        return key > 0 && reserve == key;
    }

    @Override
    public void checkWasReservedBy(int key) {
        if (key <= 0 || reserve != -key) {
            throw new ReservationException("Data was not reserved by " + reserve + " != " + key);
        }
    }

    // we don't want to add a dependency on any json framework here : we output a json like string here => need help : may be a bad idea
    final public void toString(StringBuilder buffer) {
        buffer.append("{ \"_kind\":\"").append(escapeQuote(getType().getName())).append("\"");

        GlobType type = getType();
        for (Field field : type.getFields()) {
            if (isSet(field)) {
                buffer.append(", ");
                buffer.append("\"").append(escapeQuote(field.getName())).append("\":");
                field.toString(buffer, uncheckGet(field));
            }
        }
        buffer.append("}");
    }

    private String escapeQuote(String name) {
        return name.contains("\"") ? name.replace("\"", "'") : name;
    }

    final public boolean matches(FieldValues values) {
        return values.safeApply(new Functor() {
            Boolean result = Boolean.TRUE;

            public void process(Field field, Object value) {
                if (!field.valueEqual(value, getValue(field))) {
                    result = Boolean.FALSE;
                }
            }
        }).result;
    }

    final public boolean matches(FieldValue... values) {
        for (FieldValue value : values) {
            if (!value.getField().valueEqual(value.getValue(), getValue(value.getField()))) {
                return false;
            }
        }
        return true;
    }

    final public <T extends FieldValueVisitor> T acceptOnKeyField(T functor) throws Exception {
        for (Field field : getType().getFields()) {
            field.acceptValue(functor, uncheckGet(field));
        }
        return functor;
    }

    final public <T extends FieldValueVisitor> T safeAcceptOnKeyField(T functor) {
        try {
            for (Field field : getType().getKeyFields()) {
                field.acceptValue(functor, uncheckGet(field));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return functor;
    }

    final public <T extends Functor>
    T applyOnKeyField(T functor) throws Exception {
        for (Field field : getType().getFields()) {
            functor.process(field, uncheckGet(field));
        }
        return functor;
    }

    final public <T extends Functor>
    T safeApplyOnKeyField(T functor) {
        try {
            for (Field field : getType().getKeyFields()) {
                functor.process(field, uncheckGet(field));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return functor;
    }

    final public <CTX, T extends FieldValueVisitorWithContext<CTX>>
    T acceptOnKeyField(T functor, CTX ctx) throws Exception {
        for (Field field : getType().getKeyFields()) {
            field.acceptValue(functor, uncheckGet(field), ctx);
        }
        return functor;
    }

    // implement asFieldValues for key
    final public FieldValues asFieldValues() {
        return new AbstractFieldValues() {
            GlobType type = getType();

            public boolean isSet(Field field) throws ItemNotFound {
                return AbstractDefaultGlob.this.isSet(field);
            }

            public boolean contains(Field field) {
                return field.isKeyField();
            }

            public int size() {
                return type.getKeyFields().length;
            }

            public <T extends Functor>
            T apply(T functor) throws Exception {
                for (Field field : type.getKeyFields()) {
                    functor.process(field, AbstractDefaultGlob.this.uncheckGet(field));
                }
                return functor;
            }

            public <T extends FieldValueVisitor> T accept(T functor) throws Exception {
                for (Field field : type.getKeyFields()) {
                    field.acceptValue(functor, AbstractDefaultGlob.this.uncheckGet(field));
                }
                return functor;
            }

            public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
                for (Field field : type.getKeyFields()) {
                    field.acceptValue(functor, AbstractDefaultGlob.this.uncheckGet(field), ctx);
                }
                return functor;
            }

            public FieldValue[] toArray() {
                FieldValue[] arrays = new FieldValue[type.getKeyFields().length];
                int i = 0;
                for (Field field : type.getKeyFields()) {
                    if (isSet(field)) {
                        arrays[i] = new FieldValue(field, AbstractDefaultGlob.this.uncheckGet(field));
                        i++;
                    }
                }
                return arrays;
            }

            public Object doCheckedGet(Field field) {
                return AbstractDefaultGlob.this.uncheckGet(field);
            }

        };
    }

    final public boolean contains(Field field) {
        return field.getGlobType().equals(getType());
    }

    final public int size() {
        return getType().getFieldCount();
    }

    final public GlobType getGlobType() {
        return getType();
    }

    private boolean reallyEquals(Glob glob) {
        GlobType type = getType();
        for (Field field : type.getFields()) {
            if (!field.valueEqual(getValue(field), glob.getValue(field))) {
                return false;
            }
        }
        return true;
    }

    final public MutableGlob duplicate() {
        AbstractDefaultGlob instantiate = (AbstractDefaultGlob) getType().instantiate();
        for (Field field : getType().getFields()) {
            if (isSet(field)) {
                if (isNull(field)) {
                    instantiate.uncheckedSet(field, null);
                } else {
                    // 10% faster than a switch case
                    field.safeAccept(DuplicateFieldVisitorWithTwoContext.INSTANCE, instantiate, this);
                }
            }
        }
        return instantiate;
    }


    private static class DuplicateFieldVisitorWithTwoContext implements FieldVisitorWithTwoContext<AbstractDefaultGlob, AbstractDefaultGlob> {
        static DuplicateFieldVisitorWithTwoContext INSTANCE = new DuplicateFieldVisitorWithTwoContext();

        public void visitInteger(IntegerField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitIntegerArray(IntegerArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.get(field).clone());
        }

        public void visitDouble(DoubleField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitDoubleArray(DoubleArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((double[]) src.uncheckGet(field)).clone());
        }

        public void visitBigDecimal(BigDecimalField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((BigDecimal[]) src.uncheckGet(field)).clone());
        }

        public void visitString(StringField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitStringArray(StringArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((String[]) src.uncheckGet(field)).clone());
        }

        public void visitBoolean(BooleanField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitBooleanArray(BooleanArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((boolean[]) src.uncheckGet(field)).clone());
        }

        public void visitLong(LongField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitLongArray(LongArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((long[]) src.uncheckGet(field)).clone());
        }

        public void visitDate(DateField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitDateTime(DateTimeField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, src.uncheckGet(field));
        }

        public void visitBytes(BytesField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((byte[]) src.uncheckGet(field)).clone());
        }

        public void visitGlob(GlobField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((Glob) src.uncheckGet(field)).duplicate());
        }

        public void visitGlobArray(GlobArrayField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, duplicate((Glob[]) src.uncheckGet(field)));
        }

        private static Glob[] duplicate(Glob[] globs) {
            Glob[] duplicate = new MutableGlob[globs.length];
            for (int i = 0; i < duplicate.length; i++) {
                duplicate[i] = globs[i].duplicate();
            }
            return duplicate;
        }

        public void visitUnionGlob(GlobUnionField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            instantiate.uncheckedSet(field, ((Glob) src.uncheckGet(field)).duplicate());
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, AbstractDefaultGlob instantiate, AbstractDefaultGlob src) throws Exception {
            Glob[] globs = (Glob[]) src.uncheckGet(field);
            final Glob[] duplicate = duplicate(globs);
            instantiate.uncheckedSet(field, duplicate);
        }
    }


    final public Double get(DoubleField field) {
        return (Double) doCheckedGet(field);
    }

    final public double get(DoubleField field, double valueIfNull) throws ItemNotFound {
        Object o = doCheckedGet(field);
        if (o == null) {
            return valueIfNull;
        }
        return (Double) o;
    }

    final public Integer get(IntegerField field) {
        return (Integer) doCheckedGet(field);
    }

    final public int get(IntegerField field, int valueIfNull) throws ItemNotFound {
        Integer value = (Integer) doCheckedGet(field);
        if (value == null) {
            return valueIfNull;
        }
        return value;
    }

    final public String get(StringField field) {
        return (String) doCheckedGet(field);
    }

    final public Boolean get(BooleanField field) {
        return (Boolean) doCheckedGet(field);
    }

    final public boolean isTrue(BooleanField field) {
        return Boolean.TRUE.equals(doCheckedGet(field));
    }

    final public boolean isNull(Field field) throws ItemNotFound {
        return doCheckedGet(field) == null;
    }

    final public Object getValue(Field field) {
        return doCheckedGet(field);
    }

    final public byte[] get(BytesField field) {
        return (byte[]) doCheckedGet(field);
    }

    final public boolean get(BooleanField field, boolean defaultIfNull) {
        Object value = doCheckedGet(field);
        return value == null ? Boolean.valueOf(defaultIfNull) : (boolean) value;
    }

    final public Long get(LongField field) {
        return (Long) doCheckedGet(field);
    }

    final public long get(LongField field, long valueIfNull) throws ItemNotFound {
        Object value = doCheckedGet(field);
        return value == null ? valueIfNull : (long) value;
    }

    final public double[] get(DoubleArrayField field) throws ItemNotFound {
        return (double[]) doCheckedGet(field);
    }

    final public int[] get(IntegerArrayField field) throws ItemNotFound {
        return (int[]) doCheckedGet(field);
    }

    final public String[] get(StringArrayField field) throws ItemNotFound {
        return (String[]) doCheckedGet(field);
    }

    final public boolean[] get(BooleanArrayField field) {
        return (boolean[]) doCheckedGet(field);
    }

    final public long[] get(LongArrayField field) throws ItemNotFound {
        return (long[]) doCheckedGet(field);
    }

    final public LocalDate get(DateField field) throws ItemNotFound {
        return (LocalDate) doCheckedGet(field);
    }

    final public ZonedDateTime get(DateTimeField field) throws ItemNotFound {
        return (ZonedDateTime) doCheckedGet(field);
    }

    final public BigDecimal get(BigDecimalField field) throws ItemNotFound {
        return (BigDecimal) doCheckedGet(field);
    }

    final public BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound {
        return (BigDecimal[]) doCheckedGet(field);
    }

    final public Glob get(GlobField field) throws ItemNotFound {
        return (Glob) doCheckedGet(field);
    }

    final public Glob[] get(GlobArrayField field) throws ItemNotFound {
        return (Glob[]) doCheckedGet(field);
    }

    final public Glob get(GlobUnionField field) throws ItemNotFound {
        return (Glob) doCheckedGet(field);
    }

    final public Glob[] get(GlobArrayUnionField field) throws ItemNotFound {
        return (Glob[]) doCheckedGet(field);
    }

}
