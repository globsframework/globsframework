package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.model.ReservationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class GlobReservationTest {
    @Test
    void checkReserve() {
        Stream.of(new DefaultGlob(DummyObject.TYPE),
                        new DefaultGlob128(DummyObject.TYPE),
                        new DefaultGlob64(DummyObject.TYPE))
                .forEach(mutableGlob -> {
                    mutableGlob.set(DummyObject.ID, 1);
                    mutableGlob.reserve(1);
                    try {
                        mutableGlob.reserve(2);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                    mutableGlob.set(DummyObject.ID, 2);
                    mutableGlob.release(1);
                    try {
                        mutableGlob.set(DummyObject.ID, 2);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                    try {
                        mutableGlob.get(DummyObject.ID);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                });
    }

    @Test
    void checkZeroAndMinusOneAreReserved() {
        Stream.of(new DefaultGlob(DummyObject.TYPE),
                        new DefaultGlob128(DummyObject.TYPE),
                        new DefaultGlob64(DummyObject.TYPE))
                .forEach(mutableGlob -> {
                    mutableGlob.set(DummyObject.ID, 1);
                    try {
                        mutableGlob.reserve(0);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                    try {
                        mutableGlob.reserve(-1);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                    try {
                        mutableGlob.release(-1);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                    try {
                        mutableGlob.release(0);
                        Assertions.fail();
                    } catch (ReservationException e) {
                    }
                });
    }

    @Test
    void checkIsReserved() {
        Stream.of(new DefaultGlob(DummyObject.TYPE),
                        new DefaultGlob128(DummyObject.TYPE),
                        new DefaultGlob64(DummyObject.TYPE))
                .forEach(mutableGlob -> {
                    mutableGlob.set(DummyObject.ID, 1);
                    Assertions.assertFalse(mutableGlob.isReserved());
                    mutableGlob.reserve(1);
                    Assertions.assertTrue(mutableGlob.isReserved());
                    Assertions.assertTrue(mutableGlob.isReservedBy(1));
                    Assertions.assertFalse(mutableGlob.isReservedBy(2));
                    mutableGlob.release(1);
                    Assertions.assertFalse(mutableGlob.isReserved());
                    Assertions.assertFalse(mutableGlob.isReservedBy(1));
                    Assertions.assertFalse(mutableGlob.isReservedBy(0));
                    Assertions.assertFalse(mutableGlob.isReservedBy(-1));
                });
    }
}
