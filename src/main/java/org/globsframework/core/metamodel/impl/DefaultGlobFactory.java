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
import org.globsframework.core.model.utils.FieldCheck;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

public class DefaultGlobFactory implements GlobFactory {
    private final GlobType type;
    private final DefaultGlobFactory.CreateType create;
    private final Supplier<GlobGetAccessor[]> getAccessor;
    private final Supplier<GlobSetAccessor[]> setAccessor;
//    private final StableValue<GlobGetAccessor[]> getAccessor;
//    private final StableValue<GlobSetAccessor[]> setAccessor;

    public DefaultGlobFactory(GlobType type) {
        this.type = type;
        getAccessor = new UnsafeSupplier<>(this::getGetAccessor);
        setAccessor = new UnsafeSupplier<>(this::getSetAccessor);
        create = createType();
//        getAccessor = StableValue.of();
//        setAccessor = StableValue.of();
    }

    interface CreateType {
        MutableGlob create(GlobType type);
    }

    private DefaultGlobFactory.CreateType createType() {
        final int fieldCount = type.getFieldCount();
        if (fieldCount <= 32 && Size.minSize <= 32) {
            return DefaultGlob32::new;
        }
        if (fieldCount <= 64 && Size.minSize <= 64) {
            return DefaultGlob64::new;
        }
        if (fieldCount <= 128 && Size.minSize <= 128) {
            return  DefaultGlob128::new;
        }
        return DefaultGlob::new;
    }

    private GlobSetAccessor[] getSetAccessor(){
        GlobSetAccessor[] globSetAccessors = new GlobSetAccessor[type.getFieldCount()];
        SetAccessorValueVisitor setAccessorValueVisitor = new SetAccessorValueVisitor(type);
        for (Field field : type.getFields()) {
            globSetAccessors[field.getIndex()] = field.safeAccept(setAccessorValueVisitor).setAccessor;
        }
        return globSetAccessors;
    }

    private GlobGetAccessor[] getGetAccessor() {
        GlobGetAccessor[] globGetAccessors = new GlobGetAccessor[type.getFieldCount()];
        GetAccessorValueVisitor getAccessorValueVisitor = new GetAccessorValueVisitor(type);
        for (Field field : type.getFields()) {
            globGetAccessors[field.getIndex()] = field.safeAccept(getAccessorValueVisitor).getAccessor;
        }
        return globGetAccessors;
    }

    public GlobType getGlobType() {
        return type;
    }

    public MutableGlob create(Object context) {
        return create.create(type);
    }

    public GlobSetAccessor getSetValueAccessor(Field field) {
        FieldCheck.check(field, type);
        return setAccessor.get()[field.getIndex()];
    }

    public GlobGetAccessor getGetValueAccessor(Field field) {
        FieldCheck.check(field, type);
        return getAccessor.get()[field.getIndex()];
    }

    private final static class GetAccessorValueVisitor implements FieldVisitor {
        private final GlobType type;
        public GlobGetAccessor getAccessor;

        public GetAccessorValueVisitor(GlobType type) {
            this.type = type;
        }

        public void visitInteger(IntegerField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetIntAccessor(type, field.getIndex());
        }

        public void visitIntegerArray(IntegerArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetIntArrayAccessor(type, field.getIndex());
        }

        public void visitDouble(DoubleField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDoubleAccessor(type, field.getIndex());
        }

        public void visitDoubleArray(DoubleArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDoubleArrayAccessor(type, field.getIndex());
        }

        public void visitBigDecimal(BigDecimalField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBigDecimalAccessor(type, field.getIndex());
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBigDecimalArrayAccessor(type, field.getIndex());
        }

        public void visitString(StringField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetStringAccessor(type, field.getIndex());
        }

        public void visitStringArray(StringArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetStringArrayAccessor(type, field.getIndex());
        }

        public void visitBoolean(BooleanField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBooleanAccessor(type, field.getIndex());
        }

        public void visitBooleanArray(BooleanArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBooleanArrayAccessor(type, field.getIndex());
        }

        public void visitLong(LongField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetLongAccessor(type, field.getIndex());
        }

        public void visitLongArray(LongArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetLongArrayAccessor(type, field.getIndex());
        }

        public void visitDate(DateField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDateAccessor(type, field.getIndex());
        }

        public void visitDateTime(DateTimeField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetDateTimeAccessor(type, field.getIndex());
        }

        public void visitBytes(BytesField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetBytesAccessor(type, field.getIndex());
        }

        public void visitGlob(GlobField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobAccessor(type, field.getIndex());
        }

        public void visitGlobArray(GlobArrayField field) {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobArrayAccessor(type, field.getIndex());
        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobUnionAccessor(type, field.getIndex());
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            getAccessor = new DefaultGlobFactory.DefaultGlobGetGlobUnionArrayAccessor(type, field.getIndex());
        }
    }

    private final static class SetAccessorValueVisitor implements FieldVisitor {
        private final GlobType type;
        public GlobSetAccessor setAccessor;

        public SetAccessorValueVisitor(GlobType type) {
            this.type = type;
        }

        public void visitInteger(IntegerField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetIntAccessor(type, field.getIndex());
        }

        public void visitIntegerArray(IntegerArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetIntArrayAccessor(type, field.getIndex());
        }

        public void visitDouble(DoubleField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDoubleAccessor(type, field.getIndex());
        }

        public void visitDoubleArray(DoubleArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDoubleArrayAccessor(type, field.getIndex());
        }

        public void visitBigDecimal(BigDecimalField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBigDecimalAccessor(type, field.getIndex());
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBigDecimalArrayAccessor(type, field.getIndex());
        }

        public void visitString(StringField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetStringAccessor(type, field.getIndex());
        }

        public void visitStringArray(StringArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetStringArrayAccessor(type, field.getIndex());
        }

        public void visitBoolean(BooleanField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBooleanAccessor(type, field.getIndex());
        }

        public void visitBooleanArray(BooleanArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBooleanArrayAccessor(type, field.getIndex());
        }

        public void visitLong(LongField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetLongAccessor(type, field.getIndex());
        }

        public void visitLongArray(LongArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetLongArrayAccessor(type, field.getIndex());
        }

        public void visitDate(DateField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDateAccessor(type, field.getIndex());
        }

        public void visitDateTime(DateTimeField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetDateTimeAccessor(type, field.getIndex());
        }

        public void visitBytes(BytesField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetBytesAccessor(type, field.getIndex());
        }

        public void visitGlob(GlobField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobAccessor(type, field.getIndex());
        }

        public void visitGlobArray(GlobArrayField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobArrayAccessor(type, field.getIndex());
        }

        public void visitUnionGlob(GlobUnionField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobUnionAccessor(type, field.getIndex());
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) {
            setAccessor = new DefaultGlobFactory.DefaultGlobSetGlobUnionArrayAccessor(type, field.getIndex());
        }
    }

    private final static class DefaultGlobGetIntAccessor extends AbstractGlobGetIntAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetIntAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Integer get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Integer) ag.get(index);
        }
    }

    private final static class DefaultGlobGetIntArrayAccessor extends AbstractGlobGetIntArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetIntArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public int[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (int[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetDoubleAccessor extends AbstractGlobGetDoubleAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetDoubleAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Double get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Double) ag.get(index);
        }
    }

    private final static class DefaultGlobGetDoubleArrayAccessor extends AbstractGlobGetDoubleArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetDoubleArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public double[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (double[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetBigDecimalAccessor extends AbstractGlobGetBigDecimalAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetBigDecimalAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public BigDecimal get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (BigDecimal) ag.get(index);
        }
    }

    private final static class DefaultGlobGetBigDecimalArrayAccessor extends AbstractGlobGetBigDecimalArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetBigDecimalArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public BigDecimal[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (BigDecimal[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetStringAccessor extends AbstractGlobGetStringAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetStringAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public String get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (String) ag.get(index);
        }
    }

    private final static class DefaultGlobGetStringArrayAccessor extends AbstractGlobGetStringArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetStringArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public String[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (String[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetBooleanAccessor extends AbstractGlobGetBooleanAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetBooleanAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Boolean get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Boolean) ag.get(index);
        }
    }

    private final static class DefaultGlobGetBooleanArrayAccessor extends AbstractGlobGetBooleanArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetBooleanArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public boolean[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (boolean[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetLongAccessor extends AbstractGlobGetLongAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetLongAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Long get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Long) ag.get(index);
        }
    }

    private final static class DefaultGlobGetLongArrayAccessor extends AbstractGlobGetLongArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetLongArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public long[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (long[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetDateAccessor extends AbstractGlobGetDateAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetDateAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public LocalDate get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (LocalDate) ag.get(index);
        }
    }

    private final static class DefaultGlobGetDateTimeAccessor extends AbstractGlobGetDateTimeAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetDateTimeAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public ZonedDateTime get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (ZonedDateTime) ag.get(index);
        }
    }

    private final static class DefaultGlobGetBytesAccessor extends AbstractGlobGetBytesAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetBytesAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public byte[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (byte[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetGlobAccessor extends AbstractGlobGetGlobAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetGlobAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Glob get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Glob) ag.get(index);
        }
    }

    private final static class DefaultGlobGetGlobArrayAccessor extends AbstractGlobGetGlobArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetGlobArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Glob[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Glob[]) ag.get(index);
        }
    }

    private final static class DefaultGlobGetGlobUnionAccessor extends AbstractGlobGetGlobUnionAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetGlobUnionAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Glob get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Glob) ag.get(index);
        }
    }

    private final static class DefaultGlobGetGlobUnionArrayAccessor extends AbstractGlobGetGlobUnionArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobGetGlobUnionArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean isSet(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isSetAt(index);
        }

        @Override
        public boolean isNull(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return ag.isNull(index);
        }

        public Glob[] get(Glob glob) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            return (Glob[]) ag.get(index);
        }
    }

    private final static class DefaultGlobSetIntAccessor extends AbstractGlobSetIntAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetIntAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Integer value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetIntArrayAccessor extends AbstractGlobSetIntArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetIntArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, int[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetDoubleAccessor extends AbstractGlobSetDoubleAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetDoubleAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Double value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetDoubleArrayAccessor extends AbstractGlobSetDoubleArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetDoubleArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, double[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetBigDecimalAccessor extends AbstractGlobSetBigDecimalAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetBigDecimalAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, BigDecimal value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetBigDecimalArrayAccessor extends AbstractGlobSetBigDecimalArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetBigDecimalArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, BigDecimal[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetStringAccessor extends AbstractGlobSetStringAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetStringAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, String value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }

    }

    private final static class DefaultGlobSetStringArrayAccessor extends AbstractGlobSetStringArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetStringArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, String[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetBooleanAccessor extends AbstractGlobSetBooleanAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetBooleanAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Boolean value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetBooleanArrayAccessor extends AbstractGlobSetBooleanArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetBooleanArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, boolean[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetLongAccessor extends AbstractGlobSetLongAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetLongAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Long value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetLongArrayAccessor extends AbstractGlobSetLongArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetLongArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, long[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetDateAccessor extends AbstractGlobSetDateAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetDateAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, LocalDate value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetDateTimeAccessor extends AbstractGlobSetDateTimeAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetDateTimeAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, ZonedDateTime value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetBytesAccessor extends AbstractGlobSetBytesAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetBytesAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, byte[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobAccessor extends AbstractGlobSetGlobAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetGlobAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Glob value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobArrayAccessor extends AbstractGlobSetGlobArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetGlobArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Glob[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobUnionAccessor extends AbstractGlobSetGlobUnionAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetGlobUnionAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Glob value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }

    private final static class DefaultGlobSetGlobUnionArrayAccessor extends AbstractGlobSetGlobUnionArrayAccessor {
        private final int index;
        private final GlobType type;

        public DefaultGlobSetGlobUnionArrayAccessor(GlobType type, int index) {
            this.type = type;
            this.index = index;
        }

        public void set(MutableGlob glob, Glob[] value) {
            final AbstractDefaultGlob ag = (AbstractDefaultGlob) glob;
            assert ag.getType() == type;
            ag.set(index, value);
        }
    }
}
