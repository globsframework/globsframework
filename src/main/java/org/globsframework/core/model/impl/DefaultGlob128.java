package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;

public class DefaultGlob128 extends AbstractDefaultGlob {
    protected int hashCode;
    private long set1;
    private long set2;

    public DefaultGlob128(GlobType type) {
        super(type);
    }

    public void setSetAt(int index) {
        if (index < 64) {
            set1 |= (1L << index);
        } else  {
            set2 |= (1L << (index - 64));
        }
    }

    public boolean isSetAt(int index) {
        if (index < 64) {
            return (set1 & (1L << index)) != 0;
        } else {
            return (set2 & (1L << (index - 64))) != 0;
        }
    }

    public void clearSetAt(int index) {
        if (index < 64) {
            set1 &= ~(1L << index);
        } else {
            set2 &= ~(1L << (index - 64));
        }
    }

    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }
        int hashCode = getType().hashCode();
        for (Field keyField : getType().getKeyFields()) {
            Object value = getValue(keyField);
            hashCode = 31 * hashCode + (value != null ? keyField.valueHash(value) : 0);
        }
        if (hashCode == 0) {
            hashCode = 31;
        }
        this.hashCode = hashCode;
        return hashCode;
    }

    public boolean isHashComputed() {
        return hashCode != 0;
    }

}
