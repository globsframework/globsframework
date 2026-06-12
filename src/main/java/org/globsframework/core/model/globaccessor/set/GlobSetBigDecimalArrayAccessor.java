package org.globsframework.core.model.globaccessor.set;

import org.globsframework.core.model.MutableGlob;

import java.math.BigDecimal;

public interface GlobSetBigDecimalArrayAccessor extends GlobSetAccessor {

    void set(MutableGlob glob, BigDecimal[] value);

    default void setValue(MutableGlob glob, Object value) {
        set(glob, ((BigDecimal[]) value));
    }
}
