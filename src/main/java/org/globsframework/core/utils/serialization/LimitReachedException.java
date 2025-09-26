package org.globsframework.core.utils.serialization;

class LimitReachedException extends RuntimeException {
    public LimitReachedException() {
        super("Limit reached");
    }
}
