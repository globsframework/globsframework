package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.utils.DefaultFieldValuesWithPrevious;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class FieldValuesWithPreviousBuilder {
    public static FieldValuesWithPreviousBuilder init(GlobType type) {
        return new FieldValuesWithPreviousBuilder(type);
    }

    private DefaultFieldValuesWithPrevious values;

    public FieldValuesWithPreviousBuilder(GlobType type) {
        this.values = new DefaultFieldValuesWithPrevious(type);
    }

    public void completeForCreate() {
        this.values.completeForCreate();
    }

    public void completeForUpdate() {
        this.values.completeForUpdate();
    }

    public void completeForDelete() {
        this.values.completeForDelete();
    }

    public void setValue(Field field, Object value) {
        this.values.setValue(field, value);
    }

    public void setPreviousValue(Field field, Object value) {
        this.values.setPreviousValue(field, value);
    }

    public void setValue(Field field, Object value, Object previousValue) {
        this.values.setValue(field, value, previousValue);
    }

    public FieldValuesWithPrevious get() {
        return values;
    }

    public void set(IntegerField field, Integer newValue, Integer previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(DoubleField field, Double newValue, Double previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(StringField field, String newValue, String previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(BooleanField field, Boolean newValue, Boolean previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(BytesField field, byte[] newValue, byte[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(LongField field, Long newValue, Long previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(IntegerArrayField field, int[] newValue, int[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(DoubleArrayField field, double[] newValue, double[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(BigDecimalField field, BigDecimal newValue, BigDecimal previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(LongArrayField field, long[] newValue, long[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(DateField field, LocalDate newValue, LocalDate previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(DateTimeField field, ZonedDateTime newValue, ZonedDateTime previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(StringArrayField field, String[] newValue, String[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(BigDecimalArrayField field, BigDecimal[] newValue, BigDecimal[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(BooleanArrayField field, boolean[] newValue, boolean[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(GlobField field, Glob newValue, Glob previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(GlobArrayField field, Glob[] newValue, Glob[] previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(GlobUnionField field, Glob newValue, Glob previousValue) {
        setValue(field, newValue, previousValue);
    }

    public void set(GlobArrayUnionField field, Glob[] newValue, Glob[] previousValue) {
        setValue(field, newValue, previousValue);
    }


}
