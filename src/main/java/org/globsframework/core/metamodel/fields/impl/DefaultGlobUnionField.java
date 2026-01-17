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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultGlobUnionField extends AbstractField implements GlobUnionField {
    private Supplier<GlobType>[] targetTypes;
    private volatile Map<String, GlobType> targetTypesByName = null;

    public DefaultGlobUnionField(String name, Supplier<GlobType> globType,
                                 Supplier<GlobType>[] targetTypes,
                                 int index, boolean isKeyField, final int keyIndex, HashContainer<Key, Glob> annotations) {
        super(name, globType, Glob.class, index, keyIndex, isKeyField, null, DataType.GlobUnion, annotations);
        this.targetTypes = targetTypes;
    }

    public Collection<GlobType> getTargetTypes() {
        if (targetTypesByName == null) {
            fill();
        }
        return targetTypesByName.values();
    }

    public synchronized void __add__(GlobType t) {
        if (targetTypesByName == null) {
            fill();
        }
        Map<String, GlobType> tmp = new LinkedHashMap<>(targetTypesByName);
        tmp.put(t.getName(), t);
        targetTypesByName = tmp;
    }

    private synchronized void fill() {
        if (targetTypesByName == null) {
            Map<String, GlobType> tmp = new LinkedHashMap<>((int) (targetTypes.length / 0.75));
            for (Supplier<GlobType> targetType : targetTypes) {
                tmp.put(targetType.get().getName(), targetType.get());
            }
            targetTypes = null;
            targetTypesByName = tmp;
        }
    }

    public GlobType getTargetType(String name) {
        if (targetTypesByName == null) {
            fill();
        }
        GlobType globType = targetTypesByName.get(name);
        if (globType == null) {
            throw new RuntimeException("Type " + name + " not possible in " + getFullName() + " available " + targetTypesByName.keySet());
        }
        return globType;
    }

    public <T extends FieldVisitor> T accept(T visitor) throws Exception {
        visitor.visitUnionGlob(this);
        return visitor;
    }

    public <T extends FieldVisitor> T safeAccept(T visitor) {
        try {
            visitor.visitUnionGlob(this);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T safeAccept(T visitor, C context) {
        try {
            visitor.visitUnionGlob(this, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception {
        visitor.visitUnionGlob(this, context);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception {
        visitor.visitUnionGlob(this, ctx1, ctx2);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeAccept(T visitor, C ctx1, D ctx2) {
        try {
            visitor.visitUnionGlob(this, ctx1, ctx2);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitor> T acceptValue(T visitor, Object value) throws Exception {
        visitor.visitUnionGlob(this, (Glob) value);
        return visitor;
    }

    public <T extends FieldValueVisitor> T safeAcceptValue(T visitor, Object value) {
        try {
            visitor.visitUnionGlob(this, (Glob) value);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T acceptValue(T visitor, Object value, Context context) throws Exception {
        visitor.visitUnionGlob(this, (Glob) value, context);
        return visitor;
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, Object value, Context context) {
        try {
            visitor.visitUnionGlob(this, (Glob) value, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }


    public boolean valueEqual(Object o1, Object o2) {
        return (o1 == null) && (o2 == null) ||
               (o1 instanceof Glob && o2 instanceof Glob)
               && ((Glob) o1).getType() == ((Glob) o2).getType()
               && DefaultGlobField.isSameGlob(((Glob) o1).getType(), (Glob) o1, (Glob) o2);
    }

    public boolean valueOrKeyEqual(Object o1, Object o2) {
        return (o1 == null) && (o2 == null) ||
               (o1 instanceof Glob && o2 instanceof Glob)
               && ((Glob) o1).getType() == ((Glob) o2).getType()
               && DefaultGlobField.isSameKeyOrGlob(((Glob) o1).getType(), (Glob) o1, (Glob) o2);
    }

    public void checkValue(Object object) throws InvalidParameter {
        if ((object != null) && ((!(object instanceof Glob)) || !getTargetTypes().contains(((Glob) object).getType()))) {
            throw new InvalidParameter("Value '" + object + "' (" + object.getClass().getName()
                                       + ") is not authorized for field: " + getName() +
                                       " (expected Glob)");
        }
    }

}
