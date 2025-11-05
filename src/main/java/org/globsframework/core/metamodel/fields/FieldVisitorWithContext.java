package org.globsframework.core.metamodel.fields;

public interface FieldVisitorWithContext<C> {
    void visitInteger(IntegerField field, C context) throws Exception;

    void visitIntegerArray(IntegerArrayField field, C context) throws Exception;

    void visitDouble(DoubleField field, C context) throws Exception;

    void visitDoubleArray(DoubleArrayField field, C context) throws Exception;

    void visitString(StringField field, C context) throws Exception;

    void visitStringArray(StringArrayField field, C context) throws Exception;

    void visitBoolean(BooleanField field, C context) throws Exception;

    void visitBooleanArray(BooleanArrayField field, C context) throws Exception;

    void visitBigDecimal(BigDecimalField field, C context) throws Exception;

    void visitBigDecimalArray(BigDecimalArrayField field, C context) throws Exception;

    void visitLong(LongField field, C context) throws Exception;

    void visitLongArray(LongArrayField field, C context) throws Exception;

    void visitDate(DateField field, C context) throws Exception;

    void visitDateTime(DateTimeField field, C context) throws Exception;

    void visitBytes(BytesField field, C context) throws Exception;

    void visitGlob(GlobField field, C context) throws Exception;

    void visitGlobArray(GlobArrayField field, C context) throws Exception;

    void visitUnionGlob(GlobUnionField field, C context) throws Exception;

    void visitUnionGlobArray(GlobArrayUnionField field, C context) throws Exception;

    class AbstractFieldVisitor<C> implements FieldVisitorWithContext<C> {

        public void visitInteger(IntegerField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitIntegerArray(IntegerArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitDouble(DoubleField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitDoubleArray(DoubleArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitString(StringField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitStringArray(StringArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitBoolean(BooleanField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitBooleanArray(BooleanArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitBigDecimal(BigDecimalField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitLong(LongField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitLongArray(LongArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitDate(DateField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitDateTime(DateTimeField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitBytes(BytesField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitGlob(GlobField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitGlobArray(GlobArrayField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitUnionGlob(GlobUnionField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, C context) throws Exception {
            notManaged(field, context);
        }

        public void notManaged(Field field, C context) throws Exception {
        }
    }

    class AbstractWithErrorVisitor<C> extends AbstractFieldVisitor<C> {
        public void notManaged(Field field, C context) throws Exception {
            throw new RuntimeException(field.getFullName() + " of type " + field.getDataType() + " not managed on " + getClass().getName());
        }
    }

}

