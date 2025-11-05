package org.globsframework.core.metamodel.fields;

import org.globsframework.core.metamodel.fields.impl.FieldValueVisitorButKey;
import org.globsframework.core.model.Glob;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface FieldValueVisitor {
    void visitInteger(IntegerField field, Integer value) throws Exception;

    void visitIntegerArray(IntegerArrayField field, int[] value) throws Exception;

    void visitDouble(DoubleField field, Double value) throws Exception;

    void visitDoubleArray(DoubleArrayField field, double[] value) throws Exception;

    void visitBigDecimal(BigDecimalField field, BigDecimal value) throws Exception;

    void visitBigDecimalArray(BigDecimalArrayField field, BigDecimal[] value) throws Exception;

    void visitString(StringField field, String value) throws Exception;

    void visitStringArray(StringArrayField field, String[] value) throws Exception;

    void visitBoolean(BooleanField field, Boolean value) throws Exception;

    void visitBooleanArray(BooleanArrayField field, boolean[] value) throws Exception;

    void visitLong(LongField field, Long value) throws Exception;

    void visitLongArray(LongArrayField field, long[] value) throws Exception;

    void visitDate(DateField field, LocalDate value) throws Exception;

    void visitDateTime(DateTimeField field, ZonedDateTime value) throws Exception;

    void visitBytes(BytesField field, byte[] value) throws Exception;

    void visitGlob(GlobField field, Glob value) throws Exception;

    void visitGlobArray(GlobArrayField field, Glob[] value) throws Exception;

    void visitUnionGlob(GlobUnionField field, Glob value) throws Exception;

    void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value) throws Exception;

    default FieldValueVisitor withoutKey() {
        return FieldValueVisitorButKey.create(this);
    }

    class AbstractFieldValueVisitor implements FieldValueVisitor {

        public void visitInteger(IntegerField field, Integer value) throws Exception {
            notManaged(field, value);
        }

        public void visitIntegerArray(IntegerArrayField field, int[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitDouble(DoubleField field, Double value) throws Exception {
            notManaged(field, value);
        }

        public void visitDoubleArray(DoubleArrayField field, double[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitBigDecimal(BigDecimalField field, BigDecimal value) throws Exception {
            notManaged(field, value);
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, BigDecimal[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitString(StringField field, String value) throws Exception {
            notManaged(field, value);
        }

        public void visitStringArray(StringArrayField field, String[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitBoolean(BooleanField field, Boolean value) throws Exception {
            notManaged(field, value);
        }

        public void visitBooleanArray(BooleanArrayField field, boolean[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitBytes(BytesField field, byte[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitGlob(GlobField field, Glob value) throws Exception {
            notManaged(field, value);
        }

        public void visitUnionGlob(GlobUnionField field, Glob value) throws Exception {
            notManaged(field, value);
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitGlobArray(GlobArrayField field, Glob[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitLong(LongField field, Long value) throws Exception {
            notManaged(field, value);
        }

        public void visitLongArray(LongArrayField field, long[] value) throws Exception {
            notManaged(field, value);
        }

        public void visitDate(DateField field, LocalDate value) throws Exception {
            notManaged(field, value);
        }

        public void visitDateTime(DateTimeField field, ZonedDateTime value) throws Exception {
            notManaged(field, value);
        }

        public void notManaged(Field field, Object value) throws Exception {
        }

    }

    class AbstractWithErrorVisitor extends AbstractFieldValueVisitor {
        public void notManaged(Field field, Object value) throws Exception {
            throw new RuntimeException(field.getFullName() + " of type " + field.getDataType() + " not managed.");
        }
    }
}
