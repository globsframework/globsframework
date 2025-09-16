package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.InvalidParameter;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.util.Collection;
import java.util.Collections;

public class DefaultGlobArrayField extends AbstractField implements GlobArrayField {
    private final GlobType targetType;

    public DefaultGlobArrayField(String name, GlobType globType, GlobType targetType,
                                 int index, boolean isKeyField, final int keyIndex, HashContainer<Key, Glob> annotations) {
        super(name, globType, Glob[].class, index, keyIndex, isKeyField, null, DataType.GlobArray, annotations);
        this.targetType = targetType;
    }

    public static boolean isSameGlob(GlobType type, Glob[] g1, Glob[] g2) {
        if (g1.length != g2.length) {
            return false;
        }
        for (int i = 0; i < g1.length; i++) {
            if (!DefaultGlobField.isSameGlob(type, g1[i], g2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSameKeyOrGlob(GlobType type, Glob[] g1, Glob[] g2) {
        if (g1.length != g2.length) {
            return false;
        }
        for (int i = 0; i < g1.length; i++) {
            if (!DefaultGlobField.isSameKeyOrGlob(type, g1[i], g2[i])) {
                return false;
            }
        }
        return true;
    }

    public GlobType getTargetType() {
        return targetType;
    }

    public Collection<GlobType> getTypes() {
        return Collections.singletonList(getTargetType());
    }

    public <T extends FieldVisitor> T accept(T visitor) throws Exception {
        visitor.visitGlobArray(this);
        return visitor;
    }

    public <T extends FieldVisitor> T safeAccept(T visitor) {
        try {
            visitor.visitGlobArray(this);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T safeAccept(T visitor, C context) {
        try {
            visitor.visitGlobArray(this, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception {
        visitor.visitGlobArray(this, context);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception {
        visitor.visitGlobArray(this, ctx1, ctx2);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeAccept(T visitor, C ctx1, D ctx2) {
        try {
            visitor.visitGlobArray(this, ctx1, ctx2);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitor> T acceptValue(T visitor, Object value) throws Exception {
        visitor.visitGlobArray(this, (Glob[]) value);
        return visitor;
    }

    public <T extends FieldValueVisitor> T safeAcceptValue(T visitor, Object value) {
        try {
            visitor.visitGlobArray(this, (Glob[]) value);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T acceptValue(T visitor, Object value, Context context) throws Exception {
        visitor.visitGlobArray(this, (Glob[]) value, context);
        return visitor;
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, Object value, Context context) {
        try {
            visitor.visitGlobArray(this, (Glob[]) value, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }


    public boolean valueEqual(Object o1, Object o2) {
        return (o1 == null) && (o2 == null) ||
               !((o1 == null) || (o2 == null)) &&
               isSameGlob(getTargetType(), ((Glob[]) o1), ((Glob[]) o2));
    }

    public boolean valueOrKeyEqual(Object o1, Object o2) {
        return (o1 == null) && (o2 == null) ||
               !((o1 == null) || (o2 == null)) &&
               isSameKeyOrGlob(getTargetType(), ((Glob[]) o1), ((Glob[]) o2));
    }

    public void checkValue(Object object) throws InvalidParameter {
        if ((object != null) && ((!(object instanceof Glob[])) || !checkType((Glob[]) object))) {
            throw new InvalidParameter("Value '" + object + "' (" + object.getClass().getName()
                                       + ") is not authorized for field: " + getName() +
                                       " (expected Glob)");
        }
    }

    public boolean checkType(Glob[] globs) {
        for (int i = 0; i < globs.length; i++) {
            if (globs[i] != null && getTargetType() != globs[i].getType()) {
                return false;
            }
        }
        return true;
    }

    public void toString(StringBuilder buffer, Object value) {
        if (value == null) {
            buffer.append("null");
        } else {
            toString(buffer, ((Glob[]) value));
        }
    }

    public static void toString(StringBuilder stringBuilder, Glob[] values) {
        stringBuilder.append("[");
        if (values.length != 0) {
            stringBuilder.append(values[0]);

            for (int i = 1, value1Length = values.length; i < value1Length; i++) {
                Glob glob = values[i];
                stringBuilder
                        .append(", ")
                        .append(glob);
            }
        }
        stringBuilder.append("]");
    }
}
