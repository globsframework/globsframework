package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.FieldValueVisitor;
import org.globsframework.core.metamodel.fields.FieldValueVisitorWithContext;

public interface Key extends FieldValuesAccessor, Comparable<Key> {
    GlobType getGlobType();

    <T extends FieldValues.Functor>
    T applyOnKeyField(T functor) throws Exception;

    <T extends FieldValues.Functor>
    T safeApplyOnKeyField(T functor);

    <T extends FieldValueVisitor>
    T acceptOnKeyField(T functor) throws Exception;

    <T extends FieldValueVisitor>
    T safeAcceptOnKeyField(T functor);

    <CTX, T extends FieldValueVisitorWithContext<CTX>>
    T acceptOnKeyField(T functor, CTX ctx) throws Exception;

    default <CTX, T extends FieldValueVisitorWithContext<CTX>>
     T safeAcceptOnKeyField(T functor, CTX ctx) {
       try {
           return acceptOnKeyField(functor, ctx);
       }
       catch (RuntimeException e) {
           throw e;
       }
       catch (Exception e) {
           throw new RuntimeException(e);
       }
     }

    FieldValues asFieldValues();

    default int compareTo(Key o) {
        GlobType globType = getGlobType();
        GlobType otherGlobType = o.getGlobType();
        if (globType == otherGlobType) {
            return globType.sameKeyComparator().compare(this, o);
        }
        int c = globType.getName().compareTo(otherGlobType.getName());
        if (c != 0) {
            return c;
        }
        throw new RuntimeException("Duplicate GlobType name " + globType.getName());
    }
}
