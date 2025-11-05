package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.Glob;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class FieldValueVisitorWithContextButKey<CTX> implements FieldValueVisitorWithContext<CTX> {
    private final FieldValueVisitorWithContext<CTX> fieldValueVisitor;

    public FieldValueVisitorWithContextButKey(FieldValueVisitorWithContext<CTX> fieldValueVisitor) {
        this.fieldValueVisitor = fieldValueVisitor;
    }

    public static <CTX> FieldValueVisitorWithContext<CTX> create(FieldValueVisitorWithContext<CTX> fieldValueVisitor) {
        if (fieldValueVisitor instanceof FieldValueVisitorWithContextButKey) {
            return fieldValueVisitor;
        }
        return new FieldValueVisitorWithContextButKey<CTX>(fieldValueVisitor);
    }

    @Override
    public void visitInteger(IntegerField field, Integer value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitInteger(field, value, ctx);
        }
    }

    @Override
    public void visitIntegerArray(IntegerArrayField field, int[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitIntegerArray(field, value, ctx);
        }
    }

    @Override
    public void visitDouble(DoubleField field, Double value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDouble(field, value, ctx);
        }
    }

    @Override
    public void visitDoubleArray(DoubleArrayField field, double[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDoubleArray(field, value, ctx);
        }
    }

    @Override
    public void visitBigDecimal(BigDecimalField field, BigDecimal value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBigDecimal(field, value, ctx);
        }
    }

    @Override
    public void visitBigDecimalArray(BigDecimalArrayField field, BigDecimal[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBigDecimalArray(field, value, ctx);
        }
    }

    @Override
    public void visitString(StringField field, String value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitString(field, value, ctx);
        }
    }

    @Override
    public void visitStringArray(StringArrayField field, String[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitStringArray(field, value, ctx);
        }
    }

    @Override
    public void visitBoolean(BooleanField field, Boolean value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBoolean(field, value, ctx);
        }
    }

    @Override
    public void visitBooleanArray(BooleanArrayField field, boolean[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBooleanArray(field, value, ctx);
        }
    }

    @Override
    public void visitLong(LongField field, Long value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitLong(field, value, ctx);
        }
    }

    @Override
    public void visitLongArray(LongArrayField field, long[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitLongArray(field, value, ctx);
        }
    }

    @Override
    public void visitDate(DateField field, LocalDate value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDate(field, value, ctx);
        }
    }

    @Override
    public void visitDateTime(DateTimeField field, ZonedDateTime value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDateTime(field, value, ctx);
        }
    }

    @Override
    public void visitBytes(BytesField field, byte[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBytes(field, value, ctx);
        }
    }

    @Override
    public void visitGlob(GlobField field, Glob value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitGlob(field, value, ctx);
        }
    }

    @Override
    public void visitGlobArray(GlobArrayField field, Glob[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitGlobArray(field, value, ctx);
        }
    }

    @Override
    public void visitUnionGlob(GlobUnionField field, Glob value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitUnionGlob(field, value, ctx);
        }
    }

    @Override
    public void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value, CTX ctx) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitUnionGlobArray(field, value, ctx);
        }
    }
}
