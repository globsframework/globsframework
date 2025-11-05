package org.globsframework.core.metamodel.fields;

import org.globsframework.core.model.Glob;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface FieldValueVisitorWithContext<Context> {
    void visitInteger(IntegerField field, Integer value, Context context) throws Exception;

    void visitIntegerArray(IntegerArrayField field, int[] value, Context context) throws Exception;

    void visitDouble(DoubleField field, Double value, Context context) throws Exception;

    void visitDoubleArray(DoubleArrayField field, double[] value, Context context) throws Exception;

    void visitBigDecimal(BigDecimalField field, BigDecimal value, Context context) throws Exception;

    void visitBigDecimalArray(BigDecimalArrayField field, BigDecimal[] value, Context context) throws Exception;

    void visitString(StringField field, String value, Context context) throws Exception;

    void visitStringArray(StringArrayField field, String[] value, Context context) throws Exception;

    void visitBoolean(BooleanField field, Boolean value, Context context) throws Exception;

    void visitBooleanArray(BooleanArrayField field, boolean[] value, Context context) throws Exception;

    void visitLong(LongField field, Long value, Context context) throws Exception;

    void visitLongArray(LongArrayField field, long[] value, Context context) throws Exception;

    void visitDate(DateField field, LocalDate value, Context context) throws Exception;

    void visitDateTime(DateTimeField field, ZonedDateTime value, Context context) throws Exception;

    void visitBytes(BytesField field, byte[] value, Context context) throws Exception;

    void visitGlob(GlobField field, Glob value, Context context) throws Exception;

    void visitGlobArray(GlobArrayField field, Glob[] value, Context context) throws Exception;

    void visitUnionGlob(GlobUnionField field, Glob value, Context context) throws Exception;

    void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value, Context context) throws Exception;

    default FieldValueVisitorWithContext<Context> withoutKey() {
        return FieldValueVisitorWithContextButKey.create(this);
    }

    class AbstractFieldValueVisitor<Context> implements FieldValueVisitorWithContext<Context> {

        public void visitInteger(IntegerField field, Integer value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitIntegerArray(IntegerArrayField field, int[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitDouble(DoubleField field, Double value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitDoubleArray(DoubleArrayField field, double[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitBigDecimal(BigDecimalField field, BigDecimal value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, BigDecimal[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitString(StringField field, String value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitStringArray(StringArrayField field, String[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitBoolean(BooleanField field, Boolean value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitBooleanArray(BooleanArrayField field, boolean[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitBytes(BytesField field, byte[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitGlob(GlobField field, Glob value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitUnionGlob(GlobUnionField field, Glob value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, Glob[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitGlobArray(GlobArrayField field, Glob[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitLong(LongField field, Long value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitLongArray(LongArrayField field, long[] value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitDate(DateField field, LocalDate value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void visitDateTime(DateTimeField field, ZonedDateTime value, Context context) throws Exception {
            notManaged(field, value, context);
        }

        public void notManaged(Field field, Object value, Context context) throws Exception {
        }

    }

    class AbstractWithErrorVisitor<Context> extends AbstractFieldValueVisitor<Context> {
        public void notManaged(Field field, Object value, Context context) throws Exception {
            throw new RuntimeException(field.getFullName() + " of type " + field.getDataType() + " not managed.");
        }
    }
}
