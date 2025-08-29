package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.globaccessor.get.*;
import org.globsframework.core.model.globaccessor.set.GlobSetAccessor;
import org.globsframework.core.model.globaccessor.set.GlobSetDoubleAccessor;
import org.globsframework.core.model.globaccessor.set.GlobSetIntAccessor;
import org.globsframework.core.model.globaccessor.set.GlobSetLongAccessor;

public interface GlobFactory {

    GlobType getGlobType();

    MutableGlob create(Object context);

    GlobSetAccessor getSetValueAccessor(Field field);

    GlobGetAccessor getGetValueAccessor(Field field);

    default  <T extends FieldVisitor> T accept(T visitor) throws Exception {
        Field[] fields = getGlobType().getFields();
        for (Field field : fields) {
            field.accept(visitor);
        }
        return visitor;
    }

    default  <T extends FieldVisitorWithContext<C>, C> T accept(T visitor, C context) throws Exception {
        Field[] fields = getGlobType().getFields();
        for (Field field : fields) {
            field.accept(visitor, context);
        }
        return visitor;
    }

    default  <T extends FieldVisitorWithTwoContext<C, D>, C, D> T accept(T visitor, C ctx1, D ctx2) throws Exception {
        Field[] fields = getGlobType().getFields();
        for (Field field : fields) {
            field.accept(visitor, ctx1, ctx2);
        }
        return visitor;
    }


    default <T extends FieldVisitor> T saveAccept(T visitor) {
        try {
            return accept(visitor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <T extends FieldVisitorWithContext<C>, C> T saveAccept(T visitor, C context) {
        try {
            return accept(visitor, context);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <T extends FieldVisitorWithTwoContext<C, D>, C, D> T saveAccept(T visitor, C ctx1, D ctx2) {
        try {
            return accept(visitor, ctx1, ctx2);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    default GlobSetIntAccessor getSetAccessor(IntegerField field) {
        return (GlobSetIntAccessor) getSetValueAccessor(field);
    }

    default GlobSetDoubleAccessor getSetAccessor(DoubleField field) {
        return (GlobSetDoubleAccessor) getSetValueAccessor(field);
    }

    default GlobSetLongAccessor getSetAccessor(LongField field) {
        return (GlobSetLongAccessor) getSetValueAccessor(field);
    }

    default GlobGetIntAccessor getGetAccessor(IntegerField field) {
        return (GlobGetIntAccessor) getGetValueAccessor(field);
    }

    default GlobGetStringAccessor getGetAccessor(StringField field) {
        return (GlobGetStringAccessor) getGetValueAccessor(field);
    }

    default GlobGetDoubleAccessor getGetAccessor(DoubleField field) {
        return (GlobGetDoubleAccessor) getGetValueAccessor(field);
    }

    default GlobGetLongAccessor getGetAccessor(LongField field) {
        return (GlobGetLongAccessor) getGetValueAccessor(field);
    }
}
