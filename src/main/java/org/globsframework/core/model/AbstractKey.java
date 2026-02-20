package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.impl.AbstractFieldValues;
import org.globsframework.core.model.utils.FieldCheck;
import org.globsframework.core.utils.exceptions.ItemNotFound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface AbstractKey extends Key, AbstractFieldValues {

    default boolean contains(Field field) {
        return field.getGlobType() == getGlobType() && field.isKeyField();
    }

    GlobType getGlobType();

    default <T extends FieldValueVisitor> T accept(T functor) throws Exception {
        Field[] fields = getGlobType().getKeyFields();
        for (Field field : fields) {
            field.acceptValue(functor, doGetValue(field));
        }
        return functor;
    }

    default  <CTX, T extends FieldValueVisitorWithContext<CTX>> T accept(T visitor, CTX ctx) throws Exception {
        Field[] fields = getGlobType().getKeyFields();
        for (Field field : fields) {
            field.acceptValue(visitor, doGetValue(field), ctx);
        }
        return visitor;
    }

    default  <T extends FieldValueVisitor> T acceptOnKeyField(T functor) throws Exception {
        Field[] keyFields = getGlobType().getKeyFields();
        for (Field keyField : keyFields) {
            keyField.acceptValue(functor, doGetValue(keyField));
        }
        return functor;
    }

    default  <T extends FieldValueVisitor> T safeAcceptOnKeyField(T functor) {
        try {
            Field[] keyFields = getGlobType().getKeyFields();
            for (Field keyField : keyFields) {
                keyField.acceptValue(functor, doGetValue(keyField));
            }
            return functor;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default  <T extends FieldValues.Functor>
    T applyOnKeyField(T functor) throws Exception {
        for (Field field : getGlobType().getKeyFields()) {
            functor.process(field, doGetValue(field));
        }
        return functor;
    }

    default  <T extends FieldValues.Functor>
    T safeApplyOnKeyField(T functor) {
        try {
            return applyOnKeyField(functor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default  <CTX, T extends FieldValueVisitorWithContext<CTX>>
    T acceptOnKeyField(T functor, CTX ctx) throws Exception {
        for (Field keyField : getGlobType().getKeyFields()) {
            keyField.acceptValue(functor, doGetValue(keyField), ctx);
        }
        return functor;
    }

    default FieldValues asFieldValues() {
        return this;
    }

    default Object doCheckedGet(Field field) {
        FieldCheck.checkIsKeyOf(field, getGlobType());
        return doGetValue(field);
    }

     Object doGetValue(Field field);

}
