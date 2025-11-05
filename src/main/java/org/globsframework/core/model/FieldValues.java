package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface FieldValues extends FieldValuesAccessor, FieldsValueScanner {

    FieldValues EMPTY = new FieldValues() {

        public static final FieldValue[] EMPTY_VALUES = new FieldValue[0];

        public boolean contains(Field field) {
            return false;
        }

        public int size() {
            return 0;
        }

        public <T extends Functor>
        T apply(T functor) {
            return functor;
        }

        public <T extends Functor>
        T safeApply(T functor) {
            return functor;
        }

        public <T extends FieldValueVisitor> T accept(T functor) {
            return functor;
        }
        public <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T functor, CTX ctx) throws Exception {
            return functor;
        }

        public Double get(DoubleField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public double get(DoubleField field, double valueIfNull) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public double[] get(DoubleArrayField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Integer get(IntegerField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public int get(IntegerField field, int valueIfNull) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public int[] get(IntegerArrayField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public String get(StringField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public String[] get(StringArrayField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Boolean get(BooleanField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public boolean get(BooleanField field, boolean defaultIfNull) {
            throw new ItemNotFound(field.getName());
        }

        public boolean[] get(BooleanArrayField field) {
            throw new ItemNotFound(field.getName());
        }

        public boolean isTrue(BooleanField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public boolean isSet(Field field) throws ItemNotFound {
            return false;
        }

        public boolean isNull(Field field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Object getValue(Field field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Long get(LongField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public long get(LongField field, long valueIfNull) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public long[] get(LongArrayField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public BigDecimal get(BigDecimalField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public LocalDate get(DateField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public ZonedDateTime get(DateTimeField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public byte[] get(BytesField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Glob get(GlobField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Glob[] get(GlobArrayField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Glob get(GlobUnionField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public Glob[] get(GlobArrayUnionField field) throws ItemNotFound {
            throw new ItemNotFound(field.getName());
        }

        public FieldValue[] toArray() {
            return EMPTY_VALUES;
        }

        public String toString() {
            return "";
        }

    };

    boolean contains(Field field);

    int size();

    default FieldValues withoutKeyField() {
        return new FieldValuesButKey(this);
    }

    FieldValue[] toArray();

    interface Functor {
        void process(Field field, Object value) throws Exception;

        default Functor withoutKeyField() {
            return (field, value) -> {
                if (!field.isKeyField()) {
                    Functor.this.process(field, value);
                }
            };
        }
    }

}
