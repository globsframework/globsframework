package org.globsframework.core.metamodel.fields.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.metamodel.type.DataType;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.util.Arrays;

public class DefaultBlobField extends AbstractField implements BlobField {

    public DefaultBlobField(String name, GlobType globType, int index, HashContainer<Key, Glob> annotations) {
        super(name, globType, byte[].class, index, -1, false, null, DataType.Bytes, annotations);
    }

    public <T extends FieldVisitor> T accept(T visitor) throws Exception {
        visitor.visitBlob(this);
        return visitor;
    }

    public <T extends FieldVisitor> T safeAccept(T visitor) {
        try {
            visitor.visitBlob(this);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldVisitorWithContext<C>, C> T safeAccept(T visitor, C context) {
        try {
            visitor.visitBlob(this, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    @Override
    public <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception {
        visitor.visitBlob(this, context);
        return visitor;
    }

    @Override
    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception {
        visitor.visitBlob(this, ctx1, ctx2);
        return visitor;
    }

    @Override
    public <T extends FieldVisitorWithTwoContext<C, D>, C, D> T safeAccept(T visitor, C ctx1, D ctx2) {
        try {
            visitor.visitBlob(this, ctx1, ctx2);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitor> T acceptValue(T visitor, Object value) throws Exception {
        visitor.visitBlob(this, (byte[]) value);
        return visitor;
    }

    public <T extends FieldValueVisitor> T safeAcceptValue(T visitor, Object value) {
        try {
            visitor.visitBlob(this, (byte[]) value);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T acceptValue(T visitor, Object value, Context context) throws Exception {
        visitor.visitBlob(this, (byte[]) value, context);
        return visitor;
    }

    public <T extends FieldValueVisitorWithContext<Context>, Context> T safeAcceptValue(T visitor, Object value, Context context) {
        try {
            visitor.visitBlob(this, (byte[]) value, context);
            return visitor;
        } catch (RuntimeException e) {
            throw new RuntimeException("On " + this, e);
        } catch (Exception e) {
            throw new UnexpectedApplicationState("On " + this, e);
        }
    }


    public boolean valueEqual(Object o1, Object o2) {
        return Arrays.equals(((byte[]) o1), (byte[]) o2);
    }
}
