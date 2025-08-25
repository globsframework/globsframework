package org.globsframework.core.functional;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.FieldSetter;

public interface MutableFunctionalKey extends FieldSetter<MutableFunctionalKey> {

    /*
    Warn : do not reset field values to unset.
     */
    FunctionalKey getShared();

    FunctionalKey create();

    void unset(Field field);
}
