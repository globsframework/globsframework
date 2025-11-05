package org.globsframework.core.model;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface FieldValuesWithPrevious extends FieldValues, FieldsValueWithPreviousScanner {

    Object getPreviousValue(Field field) throws ItemNotFound;

    Double getPrevious(DoubleField field) throws ItemNotFound;

    double[] getPrevious(DoubleArrayField field) throws ItemNotFound;

    double getPrevious(DoubleField field, double defaultIfNull) throws ItemNotFound;

    Integer getPrevious(IntegerField field) throws ItemNotFound;

    int[] getPrevious(IntegerArrayField field) throws ItemNotFound;

    String getPrevious(StringField field) throws ItemNotFound;

    String[] getPrevious(StringArrayField field) throws ItemNotFound;

    Boolean getPrevious(BooleanField field) throws ItemNotFound;

    Boolean[] getPrevious(BooleanArrayField field) throws ItemNotFound;

    Boolean getPrevious(BooleanField field, boolean defaultIfNull);

    Long getPrevious(LongField field) throws ItemNotFound;

    long[] getPrevious(LongArrayField field) throws ItemNotFound;

    long getPrevious(LongField field, long valueIfNull) throws ItemNotFound;

    LocalDate getPrevious(DateField field) throws ItemNotFound;

    LocalDateTime getPrevious(DateTimeField field) throws ItemNotFound;

    BigDecimal getPrevious(BigDecimalField field) throws ItemNotFound;

    BigDecimal[] getPrevious(BigDecimalArrayField field) throws ItemNotFound;

    byte[] getPrevious(BytesField field) throws ItemNotFound;

    Glob getPrevious(GlobField field) throws ItemNotFound;

    Glob[] getPrevious(GlobArrayField field) throws ItemNotFound;

    Glob getPrevious(GlobUnionField field) throws ItemNotFound;

    Glob[] getPrevious(GlobArrayUnionField field) throws ItemNotFound;

    FieldValues getPreviousValues();

    interface FunctorWithPrevious {
        void process(Field field, Object value, Object previousValue) throws Exception;

        default FunctorWithPrevious previousWithoutKey() {
            if (this instanceof WithoutKeyFunctorWithPrevious) {
                return this;
            }
            return new WithoutKeyFunctorWithPrevious(this);
        }

        class WithoutKeyFunctorWithPrevious implements FunctorWithPrevious {
            private FunctorWithPrevious functorWithPrevious;

            public WithoutKeyFunctorWithPrevious(FunctorWithPrevious functorWithPrevious) {
                this.functorWithPrevious = functorWithPrevious;
            }

            public void process(Field field, Object value, Object previousValue) throws Exception {
                if (!field.isKeyField()) {
                    functorWithPrevious.process(field, value, previousValue);
                }
            }
        }
    }
}
