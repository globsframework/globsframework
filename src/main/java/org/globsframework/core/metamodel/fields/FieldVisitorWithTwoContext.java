package org.globsframework.core.metamodel.fields;

public interface FieldVisitorWithTwoContext<C, D> {
    void visitInteger(IntegerField field, C ctx1, D ctx2) throws Exception;

    void visitIntegerArray(IntegerArrayField field, C ctx1, D ctx2) throws Exception;

    void visitDouble(DoubleField field, C ctx1, D ctx2) throws Exception;

    void visitDoubleArray(DoubleArrayField field, C ctx1, D ctx2) throws Exception;

    void visitString(StringField field, C ctx1, D ctx2) throws Exception;

    void visitStringArray(StringArrayField field, C ctx1, D ctx2) throws Exception;

    void visitBoolean(BooleanField field, C ctx1, D ctx2) throws Exception;

    void visitBooleanArray(BooleanArrayField field, C ctx1, D ctx2) throws Exception;

    void visitBigDecimal(BigDecimalField field, C ctx1, D ctx2) throws Exception;

    void visitBigDecimalArray(BigDecimalArrayField field, C ctx1, D ctx2) throws Exception;

    void visitLong(LongField field, C ctx1, D ctx2) throws Exception;

    void visitLongArray(LongArrayField field, C ctx1, D ctx2) throws Exception;

    void visitDate(DateField field, C ctx1, D ctx2) throws Exception;

    void visitDateTime(DateTimeField field, C ctx1, D ctx2) throws Exception;

    void visitBytes(BytesField field, C ctx1, D ctx2) throws Exception;

    void visitGlob(GlobField field, C ctx1, D ctx2) throws Exception;

    void visitGlobArray(GlobArrayField field, C ctx1, D ctx2) throws Exception;

    void visitUnionGlob(GlobUnionField field, C ctx1, D ctx2) throws Exception;

    void visitUnionGlobArray(GlobArrayUnionField field, C ctx1, D ctx2) throws Exception;

    class AbstractFieldVisitor<C, D> implements FieldVisitorWithTwoContext<C, D> {

        public void visitInteger(IntegerField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitIntegerArray(IntegerArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitDouble(DoubleField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitDoubleArray(DoubleArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitString(StringField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitStringArray(StringArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitBoolean(BooleanField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitBooleanArray(BooleanArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitBigDecimal(BigDecimalField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitBigDecimalArray(BigDecimalArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitLong(LongField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitLongArray(LongArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitDate(DateField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitDateTime(DateTimeField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitBytes(BytesField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitGlob(GlobField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitGlobArray(GlobArrayField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitUnionGlob(GlobUnionField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void visitUnionGlobArray(GlobArrayUnionField field, C ctx1, D ctx2) throws Exception {
            notManaged(field, ctx1, ctx2);
        }

        public void notManaged(Field field, C ctx1, D ctx2) throws Exception {
        }
    }

    class AbstractWithErrorVisitor<C, D> extends AbstractFieldVisitor<C, D> {
        public void notManaged(Field field, C ctx1, D ctx2) throws Exception {
            throw new RuntimeException(field.getFullName() + " of type " + field.getDataType() + " not managed.");
        }
    }

}

