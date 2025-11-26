package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.ReservationException;

import java.util.BitSet;

public class DefaultGlob extends AbstractDefaultGlob {
    private int hashCode;
    private final BitSet isSet;
    private int reserve = 0;

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
        isSet.clear();
        return true;
    }

    @Override
    public void unReserve() {
        hashCode = 0;
        reserve = 0;
    }

    @Override
    public boolean isReserved() {
        return reserve > 0;
    }

    @Override
    public boolean isReservedBy(int key) {
        return key > 0 && reserve == key;
    }

    @Override
    public void checkWasReservedBy(int key) {
        if (key <= 0 || reserve != -key) {
            throw new ReservationException("Data was not reserved by " + reserve + " != " + key);
        }
    }
}
