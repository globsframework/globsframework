package org.globsframework.core.metamodel.impl;

import java.util.function.Supplier;

public class UnsafeSupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T cmp = null;

    public UnsafeSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    // we don't mind having multiple instance of T
    public T get() {
        if (cmp != null) {
            return cmp;
        }
        cmp = supplier.get();
        return cmp;
    }
}
