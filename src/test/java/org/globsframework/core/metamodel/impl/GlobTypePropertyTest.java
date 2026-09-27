package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobTypePropertyTest {
    static final GlobType.Property<Object> STATIC_PROPERTY = new GlobType.Property<>();
    static final AtomicInteger typeCount = new AtomicInteger();

    // a type of its own per test, so that the state of its properties array is known
    static GlobType newType() {
        GlobTypeBuilder builder = GlobTypeBuilderFactory.create("PropertyTest" + typeCount.incrementAndGet());
        builder.declareIntegerField("id");
        return builder.build();
    }

    static class CountingBuild implements GlobType.Build<Object> {
        final AtomicInteger buildCount = new AtomicInteger();

        public Object create(GlobType globType) {
            buildCount.incrementAndGet();
            return new Object();
        }
    }

    @Test
    void buildIsCalledOnceAndValueIsCached() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        CountingBuild build = new CountingBuild();
        Object first = type.get(property, build);
        Assertions.assertSame(first, type.get(property, build));
        Assertions.assertSame(first, type.get(property, build));
        Assertions.assertEquals(1, build.buildCount.get());
    }

    @Test
    void staticPropertyKeepsOneValuePerType() {
        GlobType type1 = newType();
        GlobType type2 = newType();
        GlobType.Build<Object> build = GlobType::getName;
        Assertions.assertEquals(type1.getName(), type1.get(STATIC_PROPERTY, build));
        Assertions.assertEquals(type2.getName(), type2.get(STATIC_PROPERTY, build));
        Assertions.assertEquals(type1.getName(), type1.get(STATIC_PROPERTY, build));
    }

    @Test
    void firstBuildWins() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        CountingBuild first = new CountingBuild();
        CountingBuild second = new CountingBuild();
        Object value = type.get(property, first);
        Assertions.assertSame(value, type.get(property, second));
        Assertions.assertEquals(1, first.buildCount.get());
        Assertions.assertEquals(0, second.buildCount.get());
    }

    @Test
    void severalPropertiesAreKeptIndependently() {
        GlobType type = newType();
        GlobType.Property<Object> p1 = new GlobType.Property<>();
        GlobType.Property<Object> p2 = new GlobType.Property<>();
        CountingBuild build = new CountingBuild();
        Object v1 = type.get(p1, build);
        Object v2 = type.get(p2, build);
        Assertions.assertNotSame(v1, v2);
        Assertions.assertSame(v1, type.get(p1, build));
        Assertions.assertSame(v2, type.get(p2, build));
        Assertions.assertEquals(2, build.buildCount.get());
    }

    @Test
    void nullValueIsCached() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        AtomicInteger count = new AtomicInteger();
        GlobType.Build<Object> build = globType -> {
            count.incrementAndGet();
            return null;
        };
        Assertions.assertNull(type.get(property, build));
        Assertions.assertNull(type.get(property, build));
        Assertions.assertEquals(1, count.get());
    }

    @Test
    void reentrantBuildKeepsBothValues() {
        GlobType type = newType();
        GlobType.Property<Object> inner = new GlobType.Property<>();
        GlobType.Property<Object> outer = new GlobType.Property<>();
        CountingBuild innerBuild = new CountingBuild();
        GlobType.Build<Object> outerBuild = globType -> List.of(globType.get(inner, innerBuild));
        Object outerValue = type.get(outer, outerBuild);
        Object innerValue = type.get(inner, innerBuild);
        Assertions.assertEquals(List.of(innerValue), outerValue);
        Assertions.assertSame(outerValue, type.get(outer, outerBuild));
        Assertions.assertEquals(1, innerBuild.buildCount.get());
    }

    @Test
    void initSetsValueWithoutBuild() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        CountingBuild build = new CountingBuild();
        Object value = new Object();
        type.init(property, value);
        Assertions.assertSame(value, type.get(property, build));
        Assertions.assertEquals(0, build.buildCount.get());
    }

    @Test
    void initOnPropertyRightAfterTheLastKnownOne() {
        GlobType type = newType();
        GlobType.Property<Object> p1 = new GlobType.Property<>();
        GlobType.Property<Object> p2 = new GlobType.Property<>();
        Assertions.assertEquals(p1.index() + 1, p2.index());
        type.get(p1, new CountingBuild());    // properties array now ends at p1
        Object value = new Object();
        type.init(p2, value);
        Assertions.assertSame(value, type.get(p2, new CountingBuild()));
    }

    @Test
    void initReplacesABuiltValue() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        CountingBuild build = new CountingBuild();
        type.get(property, build);
        Object value = new Object();
        type.init(property, value);
        Assertions.assertSame(value, type.get(property, build));
        Assertions.assertEquals(1, build.buildCount.get());
    }

    @Test
    void unsetForcesANewBuild() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        CountingBuild build = new CountingBuild();
        Object first = type.get(property, build);
        type.unset(property);
        Object second = type.get(property, build);
        Assertions.assertNotSame(first, second);
        Assertions.assertEquals(2, build.buildCount.get());
    }

    @Test
    void unsetOfAnUnknownPropertyDoesNothing() {
        GlobType type = newType();
        GlobType.Property<Object> property = new GlobType.Property<>();
        type.unset(property);
        CountingBuild build = new CountingBuild();
        type.get(property, build);
        Assertions.assertEquals(1, build.buildCount.get());
    }

    @Test
    void concurrentGetBuildsOnce() throws Exception {
        int threads = 16;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            for (int round = 0; round < 100; round++) {
                GlobType type = newType();
                List<GlobType.Property<Object>> properties = new ArrayList<>();
                List<CountingBuild> builds = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    properties.add(new GlobType.Property<>());
                    builds.add(new CountingBuild());
                }
                CountDownLatch start = new CountDownLatch(1);
                List<Future<List<Object>>> futures = new ArrayList<>();
                for (int t = 0; t < threads; t++) {
                    futures.add(executor.submit(() -> {
                        start.await();
                        List<Object> values = new ArrayList<>();
                        for (int i = 0; i < properties.size(); i++) {
                            values.add(type.get(properties.get(i), builds.get(i)));
                        }
                        return values;
                    }));
                }
                start.countDown();
                List<Object> expected = futures.get(0).get();
                for (Future<List<Object>> future : futures) {
                    List<Object> values = future.get();
                    for (int i = 0; i < values.size(); i++) {
                        Assertions.assertSame(expected.get(i), values.get(i));
                    }
                }
                for (CountingBuild build : builds) {
                    Assertions.assertEquals(1, build.buildCount.get());
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
