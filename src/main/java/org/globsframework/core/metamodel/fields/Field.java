package org.globsframework.core.metamodel.fields;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.model.FieldValuesAccessor;
import org.globsframework.core.utils.exceptions.InvalidParameter;

public  interface Field extends MutableAnnotations {

    String getName();

    String getFullName();

    GlobType getGlobType();

    void checkValue(Object object) throws InvalidParameter;

    Class getValueClass();

    boolean isKeyField();

    Object getDefaultValue();

    boolean isRequired();

    <T extends FieldVisitor> T accept(T visitor) throws Exception;

    <T extends FieldVisitor> T safeAccept(T visitor);

    <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception;

    <T extends FieldVisitorWithContext<C>, C> T safeAccept(T visitor, C context);

    <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception;

    <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeAccept(T visitor, C ctx1, D ctx2);

    void accept(FieldValueVisitor visitor, Object value) throws Exception;

    void safeAccept(FieldValueVisitor visitor, Object value);

    <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, Object value, Context context);

    default <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, FieldValuesAccessor value, Context context) {
        return safeAcceptValue(visitor, value.getValue(this), context);
    }

    DataType getDataType();

    /**
     * Returns the index of the field within the containing GlobType. The order of fields
     * within a GlobType is that of the declaration. This method is mainly used for optimization purposes.
     */
    int getIndex();

    int getKeyIndex();

    boolean valueEqual(Object o1, Object o2);

    /*
    On Glob compare keys if the Glob has keys else compare all values.
     */
    boolean valueOrKeyEqual(Object o1, Object o2);

    int valueHash(Object o1);

    Object normalize(Object value);

    default StringField asStringField() {
        if (!(this instanceof StringField)) {
            throw new RuntimeException(getFullName() + " is not a StringField but a " + getDataType());
        }
        return (StringField) this;
    }

    default IntegerField asIntegerField() {
        if (!(this instanceof IntegerField)) {
            throw new RuntimeException(getFullName() + " is not a IntegerField but a " + getDataType());
        }
        return (IntegerField) this;
    }

    default BooleanField asBooleanField() {
        if (!(this instanceof BooleanField)) {
            throw new RuntimeException(getFullName() + " is not a BooleanField but a " + getDataType());
        }
        return (BooleanField) this;
    }

    default DoubleField asDoubleField() {
        if (!(this instanceof DoubleField)) {
            throw new RuntimeException(getFullName() + " is not a DoubleField but a " + getDataType());
        }
        return (DoubleField) this;
    }

    default LongField asLongField() {
        if (!(this instanceof LongField)) {
            throw new RuntimeException(getFullName() + " is not a LongField but a " + getDataType());
        }
        return (LongField) this;
    }

    default BigDecimalField asBigDecimalField() {
        if (!(this instanceof BigDecimalField)) {
            throw new RuntimeException(getFullName() + " is not a BigDecimalField but a " + getDataType());
        }
        return (BigDecimalField) this;
    }

    default DateField asDateField() {
        if (!(this instanceof DateField)) {
            throw new RuntimeException(getFullName() + " is not a DateField but a " + getDataType());
        }
        return (DateField) this;
    }

    default DateTimeField asDateTimeField() {
        if (!(this instanceof DateTimeField)) {
            throw new RuntimeException(getFullName() + " is not a DateTimeField but a " + getDataType());
        }
        return (DateTimeField) this;
    }

    default GlobArrayField asGlobArrayField() {
        if (!(this instanceof GlobArrayField)) {
            throw new RuntimeException(getFullName() + " is not a GlobArrayField but a " + getDataType());
        }
        return (GlobArrayField) this;
    }

    default GlobField asGlobField() {
        if (!(this instanceof GlobField)) {
            throw new RuntimeException(getFullName() + " is not a GlobField but a " + getDataType());
        }
        return (GlobField) this;
    }

    default GlobArrayUnionField asGlobArrayUnionField() {
        if (!(this instanceof GlobArrayUnionField)) {
            throw new RuntimeException(getFullName() + " is not a GlobArrayUnionField but a " + getDataType());
        }
        return (GlobArrayUnionField) this;
    }

    default GlobUnionField asGlobUnionField() {
        if (!(this instanceof GlobUnionField)) {
            throw new RuntimeException(getFullName() + " is not a GlobUnionField but a " + getDataType());
        }
        return (GlobUnionField) this;
    }

    default StringArrayField asStringArrayField() {
        if (!(this instanceof StringArrayField)) {
            throw new RuntimeException(getFullName() + " is not a StringArrayField but a " + getDataType());
        }
        return (StringArrayField) this;
    }

    default BooleanArrayField asBooleanArrayField() {
        if (!(this instanceof BooleanArrayField)) {
            throw new RuntimeException(getFullName() + " is not a BooleanArrayField but a " + getDataType());
        }
        return (BooleanArrayField) this;
    }

    default IntegerArrayField asIntegerArrayField() {
        if (!(this instanceof IntegerArrayField)) {
            throw new RuntimeException(getFullName() + " is not a IntegerArrayField but a " + getDataType());
        }
        return (IntegerArrayField) this;
    }

    default LongArrayField asLongArrayField() {
        if (!(this instanceof LongArrayField)) {
            throw new RuntimeException(getFullName() + " is not a LongArrayField but a " + getDataType());
        }
        return (LongArrayField) this;
    }

    default DoubleArrayField asDoubleArrayField() {
        if (!(this instanceof DoubleArrayField)) {
            throw new RuntimeException(getFullName() + " is not a DoubleArrayField but a " + getDataType());
        }
        return (DoubleArrayField) this;
    }

    default BigDecimalArrayField asBigDecimalArrayField() {
        if (!(this instanceof BigDecimalArrayField)) {
            throw new RuntimeException(getFullName() + " is not a BigDecimalArrayField but a " + getDataType());
        }
        return (BigDecimalArrayField) this;
    }

    void toString(StringBuilder buffer, Object value);
}
