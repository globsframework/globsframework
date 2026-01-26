package org.globsframework.core.utils.container;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.annotations.KeyField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.model.KeyBuilder;
import org.globsframework.core.utils.container.hash.HashContainer;
import org.globsframework.core.utils.container.specific.HashEmptyGlobContainer;
import org.openjdk.jmh.annotations.*;

import java.util.*;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@Fork(2)
@State(Scope.Thread)
public class PerfContainerTest {
    Set<Key> keys = new HashSet<>();
    private Map<Key, Glob> stableMap;
    private Map<Key, Glob> simpleMap;
    private HashContainer<Key, Glob> globContainer = HashContainer.empty();
//    private HashContainer<Key, Glob> globContainer = HashEmptyGlobContainer.Helper.allocate(2);

    @Setup
    public void setUp() {
        for (int i = 0; i < 2; i++) {
            globContainer = globContainer.put(LocalType.create(i), LocalType.create(i, "a" + i, "b" + i));
            keys.add(LocalType.create(i));
        }
//        stableMap = StableValue.map(keys, globContainer::get);
        simpleMap = new HashMap<>(stableMap);
    }

//    static void main() {
//        final PerfContainerTest perfContainerTest = new PerfContainerTest();
//        perfContainerTest.setUp();
//        perfContainerTest.testSimpleMap();
//    }

    @Benchmark
    public long testContainer() {
        long v = 0;
        for (Key key : keys) {
            v += globContainer.get(key).get(LocalType.v3);
        }
        return v;
    }

//    @Benchmark
//    public long testStableMap() {
//        long v = 0;
//        for (Key key : keys) {
//            v += stableMap.get(key).get(LocalType.v3);
//        }
//        return v;
//    }

    @Benchmark
    public long testSimpleMap() {
        long v = 0;
        for (Key key : keys) {
            v += simpleMap.get(key).get(LocalType.v3);
        }
        return v;
    }


    static class LocalType {
        public static final GlobType TYPE;

        public static final IntegerField k;

        public static final StringField v1;
        public static final StringField v2;

        public static final IntegerField v3;

        public static Key create(int i) {
            return KeyBuilder.create(TYPE, i);
        }

        public static Glob create(int i, String s1, String s2) {
            return TYPE.instantiate().set(k, i)
                    .set(v1, s1)
                    .set(v2, s2)
                    .set(v3, i * 10);
        }
        
        static {
            GlobTypeBuilder typeBuilder = new DefaultGlobTypeBuilder("LocalType");
            k = typeBuilder.declareIntegerField("k", KeyField.ZERO);
            v1 = typeBuilder.declareStringField("v1");
            v2 = typeBuilder.declareStringField("v2");
            v3 = typeBuilder.declareIntegerField("v3");
            TYPE = typeBuilder.build();
        }
    }
}
