package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.util.Arrays;

public class DefaultStringArrayField extends AbstractField implements StringArrayField {

    public DefaultStringArrayField(String name, GlobType globType, int index, boolean isKeyField, int keyIndex,
                                   String defaultValue, HashContainer<Key, Glob> annotations) {
        super(name, globType, String[].class, index, keyIndex, isKeyField, defaultValue, DataType.StringArray, annotations);
    }

    public <T extends FieldVisitor> T accept(T visitor) throws Exception {
        visitor.visitStringArray(this);
        return visitor;
    }

    public <T extends FieldVisitor> T safeAccept(T visitor) {
        try {
            visitor.visitStringArray(this);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T safeAccept(T visitor, C context) {
        try {
            visitor.visitStringArray(this, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    @Override
    public <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception {
        visitor.visitStringArray(this, context);
        return visitor;
    }

    @Override
    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception {
        visitor.visitStringArray(this, ctx1, ctx2);
        return visitor;
    }

    @Override
    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeAccept(T visitor, C ctx1, D ctx2) {
        try {
            visitor.visitStringArray(this, ctx1, ctx2);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitor> T acceptValue(T visitor, Object value) throws Exception {
        visitor.visitStringArray(this, (String[]) value);
        return visitor;
    }

    public <T extends FieldValueVisitor> T safeAcceptValue(T visitor, Object value) {
        try {
            visitor.visitStringArray(this, (String[]) value);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T acceptValue(T visitor, Object value, Context context) throws Exception {
        visitor.visitStringArray(this, (String[]) value, context);
        return visitor;
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, Object value, Context context) {
        try {
            visitor.visitStringArray(this, (String[]) value, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }


    public boolean valueEqual(Object o1, Object o2) {
        return Arrays.equals(((String[]) o1), ((String[]) o2));
    }

    public int valueHash(Object o) {
        return Arrays.hashCode(((String[]) o));
    }

    public void toString(StringBuilder buffer, Object value) {
        if (value == null) {
            buffer.append("null");
        } else {
            buffer.append("[");
            String[] values = (String[]) value;
            if (values.length > 0) {
                buffer.append("\"").append(escapeString(values[0])).append("\"");
                for (int i = 1; i < values.length; i++) {
                    String s = values[i];
                    buffer.append(", ").append("\"").append(escapeString(s)).append("\"");
                }
            }
            buffer.append("]");
        }
    }

    private String escapeString(String value) {
        return value.contains("\"") ? value.replaceAll("\"", "'") : value;
    }

}
