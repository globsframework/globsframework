package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.ReservationException;

import java.util.BitSet;

public class DefaultGlob extends AbstractDefaultGlob {
    protected int hashCode;
    protected final BitSet isSet;
    private int reserve = -1;

    public DefaultGlob(GlobType type) {
        super(type);
        isSet = new BitSet(type.getFieldCount());
    }

    public void setSetAt(int index) {
        isSet.set(index);
    }

    public boolean isSetAt(int index) {
        return isSet.get(index);
    }

    public void clearSetAt(int index) {
        isSet.clear(index);
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

    @Override
    public void checkReserved() {
        if (reserve == 0) {
            throw new ReservationException("Data not reserved");
        }
    }

    @Override
    public void reserve(int key) {
        if (key == 0 || key == -1) {
            throw new ReservationException("Reserved key 0 or -1 not valid. Got " + key);
        }
        if (reserve != 0 && reserve != -1) {
            throw new ReservationException("Already reserved by " + key);
        }
        reserve = key;
    }

    @Override
    public void release(int key) {
        if (reserve != 0 && reserve != -1) {
            if (reserve != key) {
                throw new ReservationException("Can not release data : reserved by " + reserve + " != " + key);
            }
            reserve = 0;
        } else {
            throw new ReservationException("Can not release not own Glob '" + key + "'");
        }
    }

    @Override
    public boolean isReserved() {
        return reserve != 0 && reserve != -1;
    }

    @Override
    public boolean isReservedBy(int key) {
        return reserve == key && key != -1 && key != 0;
    }
}
