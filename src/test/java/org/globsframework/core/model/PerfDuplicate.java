package org.globsframework.core.model;

import org.globsframework.core.metamodel.DummyObjectInner;
import org.globsframework.core.metamodel.DummyObjectWithInner;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@Fork(2)
@State(Scope.Thread)
public class PerfDuplicate {

    private Glob instance;

    @Setup
    public void setUp() {
        instance = DummyObjectWithInner.TYPE.instantiate()
                .set(DummyObjectWithInner.ID, 199)
                .set(DummyObjectWithInner.VALUE, DummyObjectInner.TYPE.instantiate().set(DummyObjectInner.VALUE, 1.0))
                .set(DummyObjectWithInner.VALUES, new MutableGlob[] {
                        DummyObjectInner.TYPE.instantiate().set(DummyObjectInner.VALUE, 1.0),
                        DummyObjectInner.TYPE.instantiate().set(DummyObjectInner.VALUE, 2.0),
                        DummyObjectInner.TYPE.instantiate().set(DummyObjectInner.VALUE, 3.0),
                });
    }

    @Benchmark
    public Glob duplicate() {
        return instance.duplicate();
    }
}
