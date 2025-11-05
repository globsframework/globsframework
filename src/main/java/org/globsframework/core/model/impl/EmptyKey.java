package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.FieldValue;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class EmptyKey implements Key {
    private final GlobType type;

    public EmptyKey(GlobType type) {
        this.type = type;
    }

    public GlobType getGlobType() {
        return type;
    }

    public boolean isSet(Field field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public boolean isNull(Field field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Object getValue(Field field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Double get(DoubleField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public double get(DoubleField field, double valueIfNull) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public double[] get(DoubleArrayField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Integer get(IntegerField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public int get(IntegerField field, int valueIfNull) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public int[] get(IntegerArrayField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public String get(StringField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public String[] get(StringArrayField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Boolean get(BooleanField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public boolean get(BooleanField field, boolean defaultIfNull) {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public boolean[] get(BooleanArrayField field) {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public boolean isTrue(BooleanField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Long get(LongField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public long get(LongField field, long valueIfNull) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public long[] get(LongArrayField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public BigDecimal get(BigDecimalField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public BigDecimal[] get(BigDecimalArrayField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public LocalDate get(DateField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public ZonedDateTime get(DateTimeField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public byte[] get(BytesField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Glob get(GlobField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Glob[] get(GlobArrayField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Glob get(GlobUnionField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public Glob[] get(GlobArrayUnionField field) throws ItemNotFound {
        throw new RuntimeException("Empty key '" + field.getFullName() + " not available");
    }

    public boolean contains(Field field) {
        return false;
    }

    public int size() {
        return 0;
    }

    public <T extends FieldValues.Functor> T applyOnKeyField(T functor) throws Exception {
        return functor;
    }

    public <T extends FieldValues.Functor> T safeApplyOnKeyField(T functor) {
        return functor;
    }

    public <CTX, T extends FieldValueVisitorWithContext<CTX>>
    T acceptOnKeyField(T functor, CTX ctx) throws Exception {
        return functor;
    }

    public <T extends FieldValueVisitor> T acceptOnKeyField(T functor) throws Exception {
        return functor;
    }

    public <T extends FieldValueVisitor> T safeAcceptOnKeyField(T functor) {
        return functor;
    }

    public FieldValues asFieldValues() {
        return FieldValues.EMPTY;
    }

    public FieldValue[] toArray() {
        return new FieldValue[0];
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Key key)) {
            return false;
        }

        return type == key.getGlobType();
    }

    public int hashCode() {
        return type.hashCode();
    }

    public String toString() {
        return "EmptyKey/" + type.getName();
    }
}
