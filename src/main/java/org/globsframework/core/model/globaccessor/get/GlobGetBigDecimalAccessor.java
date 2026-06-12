package org.globsframework.core.model.globaccessor.get;

import org.globsframework.core.model.Glob;

import java.math.BigDecimal;

public interface GlobGetBigDecimalAccessor extends GlobGetAccessor {
    BigDecimal get(Glob glob);

    default Object getValue(Glob glob) {
        return get(glob);
    }

}
