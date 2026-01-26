package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.model.MutableGlob;
import org.openjdk.jmh.annotations.*;

import static org.junit.jupiter.api.Assertions.*;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@Fork(2)
@State(Scope.Thread)
public class PerfKeyGlobTest {

    private MutableGlob mutableGlob;
    private Key key;

    @Setup
    public void setUp() {
        mutableGlob = DummyObject.TYPE.instantiate().set(DummyObject.ID, 1);
        this.key = KeyBuilder.create(DummyObject.TYPE, 1);
    }


    @Benchmark
    public boolean isEqualKey() {
        return mutableGlob.equals(key);
    }
}