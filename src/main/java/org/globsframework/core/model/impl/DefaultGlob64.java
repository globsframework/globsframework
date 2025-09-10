package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.ReservationException;

public class DefaultGlob64 extends AbstractDefaultGlob {
    private long set;
    private int hashCode;
    private int reserve = 0;

    public DefaultGlob64(GlobType type) {
        super(type);
    }

    public void setSetAt(int index) {
        set |= (1L << index);
    }

    public boolean isSetAt(int index) {
        return (set & (1L << index)) != 0;
    }

    public void clearSetAt(int index) {
        set &= ~(1L << index);
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
        if (reserve < 0) {
            throw new ReservationException("Data not reserved");
        }
    }

    @Override
    public void reserve(int key) {
        if (key <= 0) {
            throw new ReservationException("Reserved key <= 0 Got " + key);
        }
        if (reserve > 0) {
            throw new ReservationException("Already reserved by " + key);
        }
        reserve = key;
    }

    @Override
    public boolean release(int key) {
        if (key <= 0) {
            throw new ReservationException("Released key <= 0 Got " + key);
        }
        if (reserve == -key) {
            return false;
        }
        if (reserve != 0) {
            if (reserve != key) {
                throw new ReservationException("Can not release data : reserved by " + reserve + " != " + key);
            }
            reserve = -key;
        } else {
            throw new ReservationException("Can not release not own Glob '" + key + "'");
        }
        hashCode = 0;
        set = 0;
        return true;
    }

    @Override
    public void unReserve() {
        hashCode = 0;
        reserve = 0;
        set = 0;
    }

    @Override
    public boolean isReserved() {
        return reserve > 0;
    }

    @Override
    public boolean isReservedBy(int key) {
        return key > 0 && reserve == key;
    }

}
