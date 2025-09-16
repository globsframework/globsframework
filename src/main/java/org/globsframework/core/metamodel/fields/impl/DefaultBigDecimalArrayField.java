package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.math.BigDecimal;
import java.util.Arrays;

public class DefaultBigDecimalArrayField extends AbstractField implements BigDecimalArrayField {
    public DefaultBigDecimalArrayField(String name, GlobType globType, int index, boolean isKeyField, int keyIndex, BigDecimal defaultValue, HashContainer<Key, Glob> annotations) {
        super(name, globType, BigDecimal[].class, index, keyIndex, isKeyField, defaultValue, DataType.BigDecimalArray, annotations);
    }

    public <T extends FieldVisitor> T accept(T visitor) throws Exception {
        visitor.visitBigDecimalArray(this);
        return visitor;
    }

    public <T extends FieldVisitor> T safeAccept(T visitor) {
        try {
            visitor.visitBigDecimalArray(this);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T safeAccept(T visitor, C context) {
        try {
            visitor.visitBigDecimalArray(this, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception {
        visitor.visitBigDecimalArray(this, context);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception {
        visitor.visitBigDecimalArray(this, ctx1, ctx2);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeAccept(T visitor, C ctx1, D ctx2) {
        try {
            visitor.visitBigDecimalArray(this, ctx1, ctx2);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitor> T acceptValue(T visitor, Object value) throws Exception {
        visitor.visitBigDecimalArray(this, (BigDecimal[]) value);
        return visitor;
    }

    public <T extends FieldValueVisitor> T safeAcceptValue(T visitor, Object value) {
        try {
            visitor.visitBigDecimalArray(this, (BigDecimal[]) value);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T acceptValue(T visitor, Object value, Context context) throws Exception {
        visitor.visitBigDecimalArray(this, (BigDecimal[]) value, context);
        return visitor;
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, Object value, Context context) {
        try {
            visitor.visitBigDecimalArray(this, (BigDecimal[]) value, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }


    public boolean valueEqual(Object o1, Object o2) {
        return Arrays.equals(((BigDecimal[]) o1), ((BigDecimal[]) o2));
    }

    public int valueHash(Object o) {
        return Arrays.hashCode(((BigDecimal[]) o));
    }

    public void toString(StringBuilder buffer, Object value) {
        if (value == null) {
            buffer.append("null");
        } else {
            buffer.append(Arrays.toString(((BigDecimal[]) value)));
        }
    }
}
