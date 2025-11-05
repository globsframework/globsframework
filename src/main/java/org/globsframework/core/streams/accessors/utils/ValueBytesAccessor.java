package org.globsframework.core.streams.accessors.utils;

import org.globsframework.core.streams.accessors.BytesAccessor;

public class ValueBytesAccessor implements BytesAccessor {
    private byte[] values;

    public ValueBytesAccessor() {
    }

    public ValueBytesAccessor(byte[] values) {
        this.values = values;
    }

    public void setValue(byte[] values) {
        this.values = values;
    }

    public byte[] getValue() {
        return values;
    }

    public Object getObjectValue() {
        return values;
    }
}
