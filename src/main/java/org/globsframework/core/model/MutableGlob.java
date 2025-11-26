package org.globsframework.core.model;


import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.utils.exceptions.ItemNotFound;

public interface MutableGlob extends Glob, FieldSetter<MutableGlob> {
    MutableGlob unset(Field field);

    MutableGlob getMutable(GlobField field) throws ItemNotFound;

    MutableGlob[] getMutable(GlobArrayField field) throws ItemNotFound;

    MutableGlob getMutable(GlobUnionField field) throws ItemNotFound;

    MutableGlob[] getMutable(GlobArrayUnionField field) throws ItemNotFound;
}
