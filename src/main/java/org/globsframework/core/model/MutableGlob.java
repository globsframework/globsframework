package org.globsframework.core.model;


import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.Utils;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public interface MutableGlob extends Glob, FieldSetter<MutableGlob> {
    MutableGlob unset(Field field);

    MutableGlob getMutable(GlobField field) throws ItemNotFound;

    MutableGlob[] getMutable(GlobArrayField field) throws ItemNotFound;

    MutableGlob getMutable(GlobUnionField field) throws ItemNotFound;

    MutableGlob[] getMutable(GlobArrayUnionField field) throws ItemNotFound;

    default MutableGlob add(StringArrayField field, String...values) {
        if (values == null) {
            return this;
        }
        String[] tmp = getOrEmpty(field);
        if (tmp.length == 0) {
            set(field, values);
            return this;
        }
        else {
            set(field, Utils.join(tmp, values));
            return this;
        }
    }

    default MutableGlob add(GlobArrayField field, Glob...values) {
        if (values == null) {
            return this;
        }
        Glob[] tmp = getOrEmpty(field);
        Glob[] newValue = new MutableGlob[tmp.length + values.length];
        System.arraycopy(tmp, 0, newValue, 0, tmp.length);
        System.arraycopy(values, 0, newValue, tmp.length, values.length);
        set(field, newValue);
        return this;
    }

}
