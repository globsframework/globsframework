package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobFactory;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.globaccessor.get.GlobGetAccessor;
import org.globsframework.core.model.globaccessor.get.impl.*;
import org.globsframework.core.model.globaccessor.set.GlobSetAccessor;
import org.globsframework.core.model.globaccessor.set.impl.*;
import org.globsframework.core.model.impl.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

public class DefaultGlobFactory implements GlobFactory {
    private final GlobType type;
    private final Supplier<GlobGetAccessor[]> getAccessor;
    private final Supplier<GlobSetAccessor[]> setAccessor;
//    private final StableValue<GlobGetAccessor[]> getAccessor;
//    private final StableValue<GlobSetAccessor[]> setAccessor;

    public DefaultGlobFactory(GlobType type) {
        this.type = type;
        getAccessor = new UnsafeSupplier<>(this::getGetAccessor);
        setAccessor = new UnsafeSupplier<>(this::getSetAccessor);
//        getAccessor = StableValue.of();
//        setAccessor = StableValue.of();
    }

    private GlobSetAccessor[] getSetAccessor(){
        GlobSetAccessor[] globSetAccessors = new GlobSetAccessor[type.getFieldCount()];
        SetAccessorValueVisitor setAccessorValueVisitor = new SetAccessorValueVisitor();
        for (Field field : type.getFields()) {
            globSetAccessors[field.getIndex()] = field.safeAccept(setAccessorValueVisitor).setAccessor;
        }
        return globSetAccessors;
    }

    private GlobGetAccessor[] getGetAccessor() {
        GlobGetAccessor[] globGetAccessors = new GlobGetAccessor[type.getFieldCount()];
        GetAccessorValueVisitor getAccessorValueVisitor = new GetAccessorValueVisitor();
        for (Field field : type.getFields()) {
            globGetAccessors[field.getIndex()] = field.safeAccept(getAccessorValueVisitor).getAccessor;
        }
        return globGetAccessors;
    }

    public GlobType getGlobType() {
        return type;
    }

    public MutableGlob create(Object context) {
        if (type.getFieldCount() <= 32) {
            return new DefaultGlob32(type);
        }
        if (type.getFieldCount() <= 64) {
            return new DefaultGlob64(type);
        }
        if (type.getFieldCount() <= 128) {
            return new DefaultGlob128(type);
        }
        return new DefaultGlob(type);
    }

    public GlobSetAccessor getSetValueAccessor(Field field) {
        return setAccessor.get()[field.getIndex()];
    }

    public GlobGetAccessor getGetValueAccessor(Field field) {
        return getAccessor.get()[field.getIndex()];
    }

    private final static class GetAccessorValueVisitor implements FieldVisitor {
        public GlobGetAccessor getAccessor;

        public void visitInteger(IntegerField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetIntAccessor(field.getIndex());
        }

        public void visitIntegerArray(IntegerArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetIntArrayAccessor(field.getIndex());
        }

        public void visitDouble(DoubleField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDoubleAccessor(field.getIndex());
        }

        public void visitDoubleArray(DoubleArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDoubleArrayAccessor(field.getIndex());
        }

        public void visitBigDecimal(BigDecimalField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBigDecimalAccessor(field.getIndex());
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBigDecimalArrayAccessor(field.getIndex());
        }

        public void visitString(StringField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetStringAccessor(field.getIndex());
        }

        public void visitStringArray(StringArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetStringArrayAccessor(field.getIndex());
        }

        public void visitBoolean(BooleanField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBooleanAccessor(field.getIndex());
        }

        public void visitBooleanArray(BooleanArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBooleanArrayAccessor(field.getIndex());
        }

        public void visitLong(LongField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetLongAccessor(field.getIndex());
        }

        public void visitLongArray(LongArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetLongArrayAccessor(field.getIndex());
        }

        public void visitDate(DateField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDateAccessor(field.getIndex());
        }

        public void visitDateTime(DateTimeField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDateTimeAccessor(field.getIndex());
        }

        public void visitBytes(BytesField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBytesAccessor(field.getIndex());
        }

        public void visitGlob(GlobField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobAccessor(field.getIndex());
        }

        public void visitGlobArray(GlobArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobArrayAccessor(field.getIndex());
        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobUnionAccessor(field.getIndex());
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobUnionArrayAccessor(field.getIndex());
        }
    }

    private final static class SetAccessorValueVisitor implements FieldVisitor {
        public GlobSetAccessor setAccessor;

        public void visitInteger(IntegerField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetIntAccessor(field.getIndex());
        }

        public void visitIntegerArray(IntegerArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetIntArrayAccessor(field.getIndex());
        }

        public void visitDouble(DoubleField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDoubleAccessor(field.getIndex());
        }

        public void visitDoubleArray(DoubleArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDoubleArrayAccessor(field.getIndex());
        }

        public void visitBigDecimal(BigDecimalField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBigDecimalAccessor(field.getIndex());
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBigDecimalArrayAccessor(field.getIndex());
        }

        public void visitString(StringField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetStringAccessor(field.getIndex());
        }

        public void visitStringArray(StringArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetStringArrayAccessor(field.getIndex());
        }

        public void visitBoolean(BooleanField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBooleanAccessor(field.getIndex());
        }

        public void visitBooleanArray(BooleanArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBooleanArrayAccessor(field.getIndex());
        }

        public void visitLong(LongField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetLongAccessor(field.getIndex());
        }

        public void visitLongArray(LongArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetLongArrayAccessor(field.getIndex());
        }

        public void visitDate(DateField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDateAccessor(field.getIndex());
        }

        public void visitDateTime(DateTimeField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDateTimeAccessor(field.getIndex());
        }

        public void visitBytes(BytesField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBytesAccessor(field.getIndex());
        }

        public void visitGlob(GlobField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobAccessor(field.getIndex());
        }

        public void visitGlobArray(GlobArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobArrayAccessor(field.getIndex());
        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobUnionAccessor(field.getIndex());
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobUnionArrayAccessor(field.getIndex());
        }
    }

    private final static class DefaultGlobGetIntAccessor extends AbstractGlobGetIntAccessor {
        private final int index;

        public DefaultGlobGetIntAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Integer get(Glob glob) {
            return (Integer) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetIntArrayAccessor extends AbstractGlobGetIntArrayAccessor {
        private final int index;

        public DefaultGlobGetIntArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public int[] get(Glob glob) {
            return (int[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetDoubleAccessor extends AbstractGlobGetDoubleAccessor {
        private final int index;

        public DefaultGlobGetDoubleAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Double get(Glob glob) {
            return (Double) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetDoubleArrayAccessor extends AbstractGlobGetDoubleArrayAccessor {
        private final int index;

        public DefaultGlobGetDoubleArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public double[] get(Glob glob) {
            return (double[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetBigDecimalAccessor extends AbstractGlobGetBigDecimalAccessor {
        private final int index;

        public DefaultGlobGetBigDecimalAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public BigDecimal get(Glob glob) {
            return (BigDecimal) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetBigDecimalArrayAccessor extends AbstractGlobGetBigDecimalArrayAccessor {
        private final int index;

        public DefaultGlobGetBigDecimalArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public BigDecimal[] get(Glob glob) {
            return (BigDecimal[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetStringAccessor extends AbstractGlobGetStringAccessor {
        private final int index;

        public DefaultGlobGetStringAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public String get(Glob glob) {
            return (String)((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetStringArrayAccessor extends AbstractGlobGetStringArrayAccessor {
        private final int index;

        public DefaultGlobGetStringArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public String[] get(Glob glob) {
            return (String[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetBooleanAccessor extends AbstractGlobGetBooleanAccessor {
        private final int index;

        public DefaultGlobGetBooleanAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Boolean get(Glob glob) {
            return (Boolean) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetBooleanArrayAccessor extends AbstractGlobGetBooleanArrayAccessor {
        private final int index;

        public DefaultGlobGetBooleanArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public boolean[] get(Glob glob) {
            return (boolean[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetLongAccessor extends AbstractGlobGetLongAccessor {
        private final int index;

        public DefaultGlobGetLongAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Long get(Glob glob) {
            return (Long) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetLongArrayAccessor extends AbstractGlobGetLongArrayAccessor {
        private final int index;

        public DefaultGlobGetLongArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public long[] get(Glob glob) {
            return (long[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetDateAccessor extends AbstractGlobGetDateAccessor {
        private final int index;

        public DefaultGlobGetDateAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public LocalDate get(Glob glob) {
            return (LocalDate) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetDateTimeAccessor extends AbstractGlobGetDateTimeAccessor {
        private final int index;

        public DefaultGlobGetDateTimeAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public ZonedDateTime get(Glob glob) {
            return (ZonedDateTime) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetBytesAccessor extends AbstractGlobGetBytesAccessor {
        private final int index;

        public DefaultGlobGetBytesAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public byte[] get(Glob glob) {
            return (byte[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetGlobAccessor extends AbstractGlobGetGlobAccessor {
        private final int index;

        public DefaultGlobGetGlobAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Glob get(Glob glob) {
            return (Glob) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetGlobArrayAccessor extends AbstractGlobGetGlobArrayAccessor {
        private final int index;

        public DefaultGlobGetGlobArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Glob[] get(Glob glob) {
            return (Glob[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetGlobUnionAccessor extends AbstractGlobGetGlobUnionAccessor {
        private final int index;

        public DefaultGlobGetGlobUnionAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Glob get(Glob glob) {
            return (Glob) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobGetGlobUnionArrayAccessor extends AbstractGlobGetGlobUnionArrayAccessor {
        private final int index;

        public DefaultGlobGetGlobUnionArrayAccessor(int index) {
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            return ((AbstractDefaultGlob) glob).isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            return ((AbstractDefaultGlob) glob).isNull(index);
        }

        public Glob[] get(Glob glob) {
            return (Glob[]) ((AbstractDefaultGlob) glob).get(index);
        }
    }

    private final static class DefaultGlobSetIntAccessor extends AbstractGlobSetIntAccessor {
        private final int index;

        public DefaultGlobSetIntAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Integer value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetIntArrayAccessor extends AbstractGlobSetIntArrayAccessor {
        private final int index;

        public DefaultGlobSetIntArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, int[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetDoubleAccessor extends AbstractGlobSetDoubleAccessor {
        private final int index;

        public DefaultGlobSetDoubleAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Double value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetDoubleArrayAccessor extends AbstractGlobSetDoubleArrayAccessor {
        private final int index;

        public DefaultGlobSetDoubleArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, double[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetBigDecimalAccessor extends AbstractGlobSetBigDecimalAccessor {
        private final int index;

        public DefaultGlobSetBigDecimalAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, BigDecimal value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetBigDecimalArrayAccessor extends AbstractGlobSetBigDecimalArrayAccessor {
        private final int index;

        public DefaultGlobSetBigDecimalArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, BigDecimal[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetStringAccessor extends AbstractGlobSetStringAccessor {
        private final int index;

        public DefaultGlobSetStringAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, String value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetStringArrayAccessor extends AbstractGlobSetStringArrayAccessor {
        private final int index;

        public DefaultGlobSetStringArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, String[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetBooleanAccessor extends AbstractGlobSetBooleanAccessor {
        private final int index;

        public DefaultGlobSetBooleanAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Boolean value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetBooleanArrayAccessor extends AbstractGlobSetBooleanArrayAccessor {
        private final int index;

        public DefaultGlobSetBooleanArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, boolean[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetLongAccessor extends AbstractGlobSetLongAccessor {
        private final int index;

        public DefaultGlobSetLongAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Long value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetLongArrayAccessor extends AbstractGlobSetLongArrayAccessor {
        private final int index;

        public DefaultGlobSetLongArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, long[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetDateAccessor extends AbstractGlobSetDateAccessor {
        private final int index;

        public DefaultGlobSetDateAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, LocalDate value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetDateTimeAccessor extends AbstractGlobSetDateTimeAccessor {
        private final int index;

        public DefaultGlobSetDateTimeAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, ZonedDateTime value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetBytesAccessor extends AbstractGlobSetBytesAccessor {
        private final int index;

        public DefaultGlobSetBytesAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, byte[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobAccessor extends AbstractGlobSetGlobAccessor {
        private final int index;

        public DefaultGlobSetGlobAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Glob value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobArrayAccessor extends AbstractGlobSetGlobArrayAccessor {
        private final int index;

        public DefaultGlobSetGlobArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Glob[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobUnionAccessor extends AbstractGlobSetGlobUnionAccessor {
        private final int index;

        public DefaultGlobSetGlobUnionAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Glob value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobUnionArrayAccessor extends AbstractGlobSetGlobUnionArrayAccessor {
        private final int index;

        public DefaultGlobSetGlobUnionArrayAccessor(int index) {
            this.index = index;
        }

        public void set(MutableGlob glob, Glob[] value) {
            ((AbstractDefaultGlob) glob).set(index, value);
        }
    }
}
