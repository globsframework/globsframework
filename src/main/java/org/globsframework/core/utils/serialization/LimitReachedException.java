package org.globsframework.core.utils.serialization;

class LimitReachedException extends RuntimeException {
    public LimitReachedException(int currentPos, int limit, int count) {
        super("Limit reached, current pos: " + currentPos + " , limit: " + limit + " available: " + count);
    }
}
