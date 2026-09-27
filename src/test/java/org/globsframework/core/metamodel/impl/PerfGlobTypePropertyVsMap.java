package org.globsframework.core.metamodel.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/*
Per-type value lookup: T t = type.get(property) vs T t = map.get(type).
 - property   : GlobType.get(PROPERTY, BUILD), static property and non capturing build, through the GlobType interface
 - propertyCapturing: the same with a build lambda capturing a local, i.e. a new lambda on every call
 - concurrentCompute: ConcurrentHashMap.computeIfAbsent, the map doing the same job as get(property, build)
 - hashMap    : HashMap<GlobType, Object> (DefaultGlobType keeps the identity hashCode / equals)
 - identityMap: IdentityHashMap<GlobType, Object>
 - concurrent : ConcurrentHashMap<GlobType, Object>, what a map filled lazily from several threads needs

 "single" asks the same type every time, "loop" LOOP lookups per op going round typeCount types.

run: mvn -o test-compile && java -cp target/test-classes:target/classes:$(cat cp.txt) \
        org.globsframework.core.metamodel.impl.PerfGlobTypePropertyVsMap
 (cp.txt from: mvn -o dependency:build-classpath -Dmdep.outputFile=cp.txt -Dmdep.includeScope=test)
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(2)
@State(Scope.Benchmark)
public class PerfGlobTypePropertyVsMap {
    static final int LOOP = 128;
    static final GlobType.Property<Object> PROPERTY = new GlobType.Property<>();
    static final GlobType.Build<Object> BUILD = globType -> new Object();
    static final Function<GlobType, Object> COMPUTE = BUILD::create;

    @Param({"16", "1024"})
    int typeCount;

    private GlobType[] types;
    private Map<GlobType, Object> hashMap;
    private Map<GlobType, Object> identityMap;
    private ConcurrentHashMap<GlobType, Object> concurrentMap;
    private int mask;
    private int start;

    @Setup
    public void setUp() {
        types = new GlobType[typeCount];
        hashMap = new HashMap<>();
        identityMap = new IdentityHashMap<>();
        concurrentMap = new ConcurrentHashMap<>();
        for (int i = 0; i < typeCount; i++) {
            GlobTypeBuilder builder = GlobTypeBuilderFactory.create("PerfType" + i);
            builder.declareIntegerField("id");
            GlobType type = builder.build();
            types[i] = type;
            Object value = type.get(PROPERTY, BUILD);
            hashMap.put(type, value);
            identityMap.put(type, value);
            concurrentMap.put(type, value);
        }
        mask = typeCount - 1;
    }

    @Benchmark
    public Object singleProperty() {
        return types[0].get(PROPERTY, BUILD);
    }

    @Benchmark
    public Object singlePropertyCapturing() {
        Object fallback = types;
        return types[0].get(PROPERTY, globType -> fallback);
    }

    @Benchmark
    public Object singleHashMap() {
        return hashMap.get(types[0]);
    }

    @Benchmark
    public Object singleIdentityMap() {
        return identityMap.get(types[0]);
    }

    @Benchmark
    public Object singleConcurrentMap() {
        return concurrentMap.get(types[0]);
    }

    @Benchmark
    @OperationsPerInvocation(LOOP)
    public void loopProperty(Blackhole bh) {
        GlobType[] types = this.types;
        int s = start;
        for (int i = 0; i < LOOP; i++) {
            bh.consume(types[(s + i) & mask].get(PROPERTY, BUILD));
        }
        start = s + LOOP;
    }

    @Benchmark
    @OperationsPerInvocation(LOOP)
    public void loopPropertyCapturing(Blackhole bh) {
        GlobType[] types = this.types;
        int s = start;
        for (int i = 0; i < LOOP; i++) {
            int j = (s + i) & mask;
            bh.consume(types[j].get(PROPERTY, globType -> j));
        }
        start = s + LOOP;
    }

    @Benchmark
    @OperationsPerInvocation(LOOP)
    public void loopConcurrentCompute(Blackhole bh) {
        GlobType[] types = this.types;
        ConcurrentHashMap<GlobType, Object> map = concurrentMap;
        int s = start;
        for (int i = 0; i < LOOP; i++) {
            bh.consume(map.computeIfAbsent(types[(s + i) & mask], COMPUTE));
        }
        start = s + LOOP;
    }

    @Benchmark
    @OperationsPerInvocation(LOOP)
    public void loopHashMap(Blackhole bh) {
        GlobType[] types = this.types;
        Map<GlobType, Object> map = hashMap;
        int s = start;
        for (int i = 0; i < LOOP; i++) {
            bh.consume(map.get(types[(s + i) & mask]));
        }
        start = s + LOOP;
    }

    @Benchmark
    @OperationsPerInvocation(LOOP)
    public void loopIdentityMap(Blackhole bh) {
        GlobType[] types = this.types;
        Map<GlobType, Object> map = identityMap;
        int s = start;
        for (int i = 0; i < LOOP; i++) {
            bh.consume(map.get(types[(s + i) & mask]));
        }
        start = s + LOOP;
    }

    @Benchmark
    @OperationsPerInvocation(LOOP)
    public void loopConcurrentMap(Blackhole bh) {
        GlobType[] types = this.types;
        Map<GlobType, Object> map = concurrentMap;
        int s = start;
        for (int i = 0; i < LOOP; i++) {
            bh.consume(map.get(types[(s + i) & mask]));
        }
        start = s + LOOP;
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(PerfGlobTypePropertyVsMap.class.getSimpleName())
                .build()).run();
    }
}
