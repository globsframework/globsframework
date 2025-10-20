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
import org.globsframework.core.model.impl.AbstractDefaultGlob;
import org.globsframework.core.model.impl.DefaultGlob;
import org.globsframework.core.model.impl.DefaultGlob128;
import org.globsframework.core.model.impl.DefaultGlob64;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class DefaultGlobFactory implements GlobFactory {
    private final GlobType type;
    private GlobGetAccessor[] getAccessor;
    private GlobSetAccessor[] setAccessor;

    public DefaultGlobFactory(GlobType type) {
        this.type = type;
    }

    private void initAccessor(GlobType type) {
        GlobGetAccessor[] getAccessor1 = new GlobGetAccessor[type.getFieldCount()];
        GetAccessorValueVisitor getAccessorValueVisitor = new GetAccessorValueVisitor();
        SetAccessorValueVisitor setAccessorValueVisitor = new SetAccessorValueVisitor();
        GlobSetAccessor[] setAccessor1 = new GlobSetAccessor[type.getFieldCount()];
        for (Field field : type.getFields()) {
            getAccessor1[field.getIndex()] = field.safeAccept(getAccessorValueVisitor).getAccessor;
            setAccessor1[field.getIndex()] = field.safeAccept(setAccessorValueVisitor).setAccessor;
        }

        synchronized (this) {  //I don't know if this enough nether necessary
            if (getAccessor == null) {
                getAccessor = getAccessor1;
            }
            if (setAccessor == null) {
                setAccessor = setAccessor1;
            }
        }
    }

    public GlobType getGlobType() {
        return type;
    }

    public MutableGlob create(Object context) {
        if (type.getFieldCount() <= 64) {
            return new DefaultGlob64(type);
        }
        if (type.getFieldCount() <= 128) {
            return new DefaultGlob128(type);
        }
        return new DefaultGlob(type);
    }

    public GlobSetAccessor getSetValueAccessor(Field field) {
        if (setAccessor == null) {
            initAccessor(type);
        }
        return setAccessor[field.getIndex()];
    }

    public GlobGetAccessor getGetValueAccessor(Field field) {
        if (getAccessor == null) {
            initAccessor(type);
        }
        return getAccessor[field.getIndex()];
    }

    private static class GetAccessorValueVisitor implements FieldVisitor {
        public GlobGetAccessor getAccessor;

        public void visitInteger(IntegerField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetIntAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Integer get(Glob glob) {
                    return (Integer) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitIntegerArray(IntegerArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetIntArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public int[] get(Glob glob) {
                    return (int[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitDouble(DoubleField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetDoubleAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Double get(Glob glob) {
                    return (Double) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitDoubleArray(DoubleArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetDoubleArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public double[] get(Glob glob) {
                    return (double[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitBigDecimal(BigDecimalField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetBigDecimalAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }

                public BigDecimal get(Glob glob) {
                    return (BigDecimal) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetBigDecimalArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public BigDecimal[] get(Glob glob) {
                    return (BigDecimal[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitString(StringField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetStringAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public String get(Glob glob) {
                    return (String)((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitStringArray(StringArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetStringArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public String[] get(Glob glob) {
                    return (String[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitBoolean(BooleanField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetBooleanAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Boolean get(Glob glob) {
                    return (Boolean) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitBooleanArray(BooleanArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetBooleanArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public boolean[] get(Glob glob) {
                    return (boolean[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitLong(LongField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetLongAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Long get(Glob glob) {
                    return (Long) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitLongArray(LongArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetLongArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public long[] get(Glob glob) {
                    return (long[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitDate(DateField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetDateAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public LocalDate get(Glob glob) {
                    return (LocalDate) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitDateTime(DateTimeField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetDateTimeAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public ZonedDateTime get(Glob glob) {
                    return (ZonedDateTime) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitBlob(BlobField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetBytesAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public byte[] get(Glob glob) {
                    return (byte[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitGlob(GlobField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetGlobAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Glob get(Glob glob) {
                    return (Glob) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitGlobArray(GlobArrayField field) {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetGlobArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Glob[] get(Glob glob) {
                    return (Glob[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetGlobUnionAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Glob get(Glob glob) {
                    return (Glob) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            final int index = field.getIndex();
            getAccessor = new AbstractGlobGetGlobUnionArrayAccessor() {
                @Override
                public boolean isSet(Glob glob) {
                    return ((AbstractDefaultGlob) glob).isSetAt(index);
                }
                public Glob[] get(Glob glob) {
                    return (Glob[]) ((AbstractDefaultGlob) glob).get(index);
                }
            };
        }
    }

    private static class SetAccessorValueVisitor implements FieldVisitor {
        public GlobSetAccessor setAccessor;

        public void visitInteger(IntegerField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetIntAccessor() {
                public void set(MutableGlob glob, Integer value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitIntegerArray(IntegerArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetIntArrayAccessor() {
                public void set(MutableGlob glob, int[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitDouble(DoubleField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetDoubleAccessor() {
                public void set(MutableGlob glob, Double value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitDoubleArray(DoubleArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetDoubleArrayAccessor() {
                public void set(MutableGlob glob, double[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitBigDecimal(BigDecimalField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetBigDecimalAccessor() {
                public void set(MutableGlob glob, BigDecimal value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitBigDecimalArray(BigDecimalArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetBigDecimalArrayAccessor() {
                public void set(MutableGlob glob, BigDecimal[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitString(StringField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetStringAccessor() {
                public void set(MutableGlob glob, String value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitStringArray(StringArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetStringArrayAccessor() {
                public void set(MutableGlob glob, String[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitBoolean(BooleanField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetBooleanAccessor() {
                public void set(MutableGlob glob, Boolean value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitBooleanArray(BooleanArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetBooleanArrayAccessor() {
                public void set(MutableGlob glob, boolean[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitLong(LongField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetLongAccessor() {
                public void set(MutableGlob glob, Long value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitLongArray(LongArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetLongArrayAccessor() {
                public void set(MutableGlob glob, long[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitDate(DateField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetDateAccessor() {
                public void set(MutableGlob glob, LocalDate value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitDateTime(DateTimeField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetDateTimeAccessor() {
                public void set(MutableGlob glob, ZonedDateTime value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitBlob(BlobField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetBytesAccessor() {
                public void set(MutableGlob glob, byte[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitGlob(GlobField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetGlobAccessor() {
                public void set(MutableGlob glob, Glob value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitGlobArray(GlobArrayField field) {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetGlobArrayAccessor() {
                public void set(MutableGlob glob, Glob[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetGlobUnionAccessor() {
                public void set(MutableGlob glob, Glob value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            final int index = field.getIndex();
            setAccessor = new AbstractGlobSetGlobUnionArrayAccessor() {
                public void set(MutableGlob glob, Glob[] value) {
                    ((AbstractDefaultGlob) glob).set(index, value);
                }
            };
        }
    }
}
