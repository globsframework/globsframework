package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class FieldValueVisitorButKey implements FieldValueVisitor {
    private final FieldValueVisitor fieldValueVisitor;

    private FieldValueVisitorButKey(FieldValueVisitor fieldValueVisitor) {
        this.fieldValueVisitor = fieldValueVisitor;
    }

    public FieldValueVisitor inner() {
        return fieldValueVisitor;
    }

    public static FieldValueVisitor create(FieldValueVisitor fieldValueVisitor) {
        if (fieldValueVisitor instanceof FieldValueVisitorButKey) {
            return fieldValueVisitor;
        }
        return new FieldValueVisitorButKey(fieldValueVisitor);
    }


    public void visitInteger(IntegerField field, Integer value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitInteger(field, value);
        }
    }

    public void visitIntegerArray(IntegerArrayField field, int[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitIntegerArray(field, value);
        }
    }

    public void visitDouble(DoubleField field, Double value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDouble(field, value);
        }
    }

    public void visitDoubleArray(DoubleArrayField field, double[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDoubleArray(field, value);
        }
    }

    public void visitBigDecimal(BigDecimalField field, BigDecimal value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBigDecimal(field, value);
        }
    }

    public void visitBigDecimalArray(BigDecimalArrayField field, BigDecimal[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBigDecimalArray(field, value);
        }
    }

    public void visitString(StringField field, String value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitString(field, value);
        }
    }

    public void visitStringArray(StringArrayField field, String[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitStringArray(field, value);
        }
    }

    public void visitBoolean(BooleanField field, Boolean value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBoolean(field, value);
        }
    }

    public void visitBooleanArray(BooleanArrayField field, boolean[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBooleanArray(field, value);
        }
    }

    public void visitLong(LongField field, Long value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitLong(field, value);
        }
    }

    public void visitLongArray(LongArrayField field, long[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitLongArray(field, value);
        }
    }

    public void visitDate(DateField field, LocalDate value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDate(field, value);
        }
    }

    public void visitDateTime(DateTimeField field, ZonedDateTime value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitDateTime(field, value);
        }
    }

    public void visitBytes(BytesField field, byte[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitBytes(field, value);
        }
    }

    public void visitGlob(GlobField field, Glob value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitGlob(field, value);
        }
    }

    public void visitGlobArray(GlobArrayField field, Glob[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitGlobArray(field, value);
        }
    }

    public void visitUnionGlob(GlobUnionField field, Glob value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitUnionGlob(field, value);
        }
    }

    public void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value) throws Exception {
        if (!field.isKeyField()) {
            fieldValueVisitor.visitUnionGlobArray(field, value);
        }
    }
}
