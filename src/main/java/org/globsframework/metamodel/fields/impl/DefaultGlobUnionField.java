package org.globsframework.metamodel.fields.impl;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.fields.*;
import org.globsframework.metamodel.type.DataType;
import org.globsframework.model.Glob;
import org.globsframework.utils.exceptions.InvalidParameter;
import org.globsframework.utils.exceptions.UnexpectedApplicationState;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultGlobUnionField extends AbstractField implements GlobUnionField {
    private Map<String, GlobType> targetTypes;

    public DefaultGlobUnionField(String name, GlobType globType, List<GlobType> targetTypes,
                                 int index, boolean isKeyField, final int keyIndex) {
        super(name, globType, Glob.class, index, keyIndex, isKeyField, null, DataType.GlobUnion);
        this.targetTypes = new HashMap<>();
        targetTypes.forEach(this::__add__);
    }

    public Collection<GlobType> getTargetTypes() {
        return targetTypes.values();
    }

    public void __add__(GlobType t) {
        this.targetTypes.put(t.getName(), t);
    }

    public GlobType getTargetType(String name) {
        GlobType globType = targetTypes.get(name);
        if (globType == null) {
            throw new RuntimeException("Type " + name + " not possible in " + getFullName() + " available " + targetTypes);
        }
        return globType;
    }

    public <T extends FieldVisitor> T visit(T visitor) throws Exception {
        visitor.visitUnionGlob(this);
        return visitor;
    }

    public <T extends FieldVisitor> T safeVisit(T visitor) {
        try {
            visitor.visitUnionGlob(this);
            return visitor;
        }
        catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        }
        catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T safeVisit(T visitor, C context) {
        try {
            visitor.visitUnionGlob(this, context);
            return visitor;
        }
        catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        }
        catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T visit(T visitor, C context) throws Exception {
        visitor.visitUnionGlob(this, context);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T visit(T visitor, C ctx1, D ctx2) throws Exception {
        visitor.visitUnionGlob(this, ctx1, ctx2);
        return visitor;
    }

    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeVisit(T visitor, C ctx1, D ctx2) {
        try {
            visitor.visitUnionGlob(this, ctx1, ctx2);
            return visitor;
        }
        catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        }
        catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public void visit(FieldValueVisitor visitor, Object value) throws Exception {
        visitor.visitUnionGlob(this, (Glob)value);
    }

    public void safeVisit(FieldValueVisitor visitor, Object value) {
        try {
            visitor.visitUnionGlob(this, (Glob)value);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        }
        catch (Exception e) {
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
        if ((object != null) && (!(object instanceof Glob))) {
            throw new InvalidParameter("Value '" + object + "' (" + object.getClass().getName()
                    + ") is not authorized for field: " + getName() +
                    " (expected Glob)");
        }
    }

}
