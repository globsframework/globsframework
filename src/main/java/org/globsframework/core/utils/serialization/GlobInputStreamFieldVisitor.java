package org.globsframework.core.utils.serialization;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;

class GlobInputStreamFieldVisitor implements FieldVisitorWithContext<MutableGlob> {
    private final SerializedInput serializedInput;
    private final GlobDeSerializer globDeSerializer;

    public GlobInputStreamFieldVisitor(SerializedInput serializedInput, GlobDeSerializer globDeSerializer) {
        this.serializedInput = serializedInput;
        this.globDeSerializer = globDeSerializer;
    }

    public void visitInteger(IntegerField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readInteger());
    }

    public void visitIntegerArray(IntegerArrayField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readIntArray());
    }

    public void visitDouble(DoubleField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readDouble());
    }

    public void visitDoubleArray(DoubleArrayField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readDoubleArray());
    }

    public void visitBigDecimal(BigDecimalField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readBigDecimal());
    }

    public void visitBigDecimalArray(BigDecimalArrayField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readBigDecimalArray());
    }

    public void visitString(StringField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readUtf8String());
    }

    public void visitStringArray(StringArrayField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readStringArray());
    }

    public void visitBoolean(BooleanField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readBoolean());
    }

    public void visitBooleanArray(BooleanArrayField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readBooleanArray());
    }

    public void visitBlob(BlobField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readBytes());
    }

    public void visitGlob(GlobField field, MutableGlob mutableGlob) {
        if (serializedInput.readBoolean()) {
            mutableGlob.set(field, globDeSerializer.readKnowGlob(field.getTargetType()));
        }
    }

    public void visitGlobArray(GlobArrayField field, MutableGlob mutableGlob) {
        int len = serializedInput.readNotNullInt();
        if (len >= 0) {
            Glob[] values = new Glob[len];
            for (int i = 0; i < values.length; i++) {
                if (serializedInput.readBoolean()) {
                    values[i] = globDeSerializer.readKnowGlob(field.getTargetType());
                }
            }
            mutableGlob.set(field, values);
        }
    }

    public void visitUnionGlob(GlobUnionField field, MutableGlob mutableGlob) {
        if (serializedInput.readBoolean()) {
            GlobType globType = field.getTargetType(serializedInput.readUtf8String());
            mutableGlob.set(field, globDeSerializer.readKnowGlob(globType));
        }
    }

    public void visitUnionGlobArray(GlobArrayUnionField field, MutableGlob mutableGlob) {
        int len = serializedInput.readNotNullInt();
        if (len >= 0) {
            Glob[] values = new Glob[len];
            for (int i = 0; i < values.length; i++) {
                if (serializedInput.readBoolean()) {
                    values[i] = globDeSerializer.readKnowGlob(field.getTargetType(serializedInput.readUtf8String()));
                }
            }
            mutableGlob.set(field, values);
        }
    }

    public void visitLong(LongField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readLong());
    }

    public void visitLongArray(LongArrayField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readLongArray());
    }

    public void visitDate(DateField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readDate());
    }

    public void visitDateTime(DateTimeField field, MutableGlob mutableGlob) {
        mutableGlob.set(field, serializedInput.readDateTime());
    }
}
