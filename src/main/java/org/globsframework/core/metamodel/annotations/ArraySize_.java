package org.globsframework.core.metamodel.annotations;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.model.Key;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ArraySize_ {
    int value();

    GlobType GLOB_TYPE = ArraySize.TYPE;

    Key KEY = ArraySize.KEY;

}
