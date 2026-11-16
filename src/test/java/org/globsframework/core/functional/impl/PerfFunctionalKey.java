package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKey;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
ManyFieldsMutableKey (array + index table) vs the unrolled Three/FourFieldsMutableKey, on ID/NAME/VALUE(/COUNT) of DummyObject.
 - create : builder.create(glob), the allocation a map entry costs
 - lookup : fill a shared mutable key from a glob, then HashMap.get (hashCode + equals)
 - read   : get the three fields back from a key
 - check  : globs-shared's HashReadIndex.readAndCheck loop, getValue over getBuilder().getFields()
 - matches: the same check through FunctionalKey.matches
SIZE globs per op, so the numbers are per SIZE.

run: mvn -o test-compile && java -cp target/test-classes:target/classes:$(cat cp.txt) \
        org.globsframework.core.functional.impl.PerfFunctionalKey
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(2)
@State(Scope.Benchmark)
public class PerfFunctionalKey {
    static final int SIZE = 1024;

    @Param({"many3", "three", "many4", "four"})
    String impl;

    FunctionalKeyBuilder builder;
    Glob[] globs;
    FunctionalKey[] keys;
    Map<FunctionalKey, Glob> map;
    MutableFunctionalKey shared;
    boolean fourFields;

    @Setup
    public void setup() {
        Field[] three = {DummyObject.ID, DummyObject.NAME, DummyObject.VALUE};
        Field[] four = {DummyObject.ID, DummyObject.NAME, DummyObject.VALUE, DummyObject.COUNT};
        builder = switch (impl) {
            case "many3" -> new ManyFunctionalKeyBuilder(three);
            case "three" -> new ThreeFunctionalKeyBuilder(three[0], three[1], three[2]);
            case "many4" -> new ManyFunctionalKeyBuilder(four);
            case "four" -> new FourFunctionalKeyBuilder(four[0], four[1], four[2], four[3]);
            default -> throw new IllegalArgumentException(impl);
        };
        globs = new Glob[SIZE];
        keys = new FunctionalKey[SIZE];
        map = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            MutableGlob g = DummyObject.TYPE.instantiate()
                    .set(DummyObject.ID, i).set(DummyObject.NAME, "name-" + (i % 37)).set(DummyObject.VALUE, i * 1.5).set(DummyObject.COUNT, i % 11);
            globs[i] = g;
            keys[i] = builder.create(g);
            map.put(keys[i], g);
        }
        shared = builder.create();
        fourFields = builder.getFields().length == 4;
    }

    @Benchmark
    public FunctionalKey[] create() {
        FunctionalKey[] r = new FunctionalKey[SIZE];
        for (int i = 0; i < SIZE; i++) {
            r[i] = builder.create(globs[i]);
        }
        return r;
    }

    @Benchmark
    public int lookup() {
        int found = 0;
        for (Glob g : globs) {
            shared.set(DummyObject.ID, g.get(DummyObject.ID))
                    .set(DummyObject.NAME, g.get(DummyObject.NAME))
                    .set(DummyObject.VALUE, g.get(DummyObject.VALUE));
            if (fourFields) {
                shared.set(DummyObject.COUNT, g.get(DummyObject.COUNT));
            }
            if (map.get(shared) != null) found++;
        }
        return found;
    }

    @Benchmark
    public long read() {
        long s = 0;
        for (FunctionalKey k : keys) {
            s += k.get(DummyObject.ID) + k.get(DummyObject.NAME).length() + k.get(DummyObject.VALUE).longValue();
        }
        return s;
    }

    /** What globs-shared's HashReadIndex.readAndCheck does on each candidate: every field of the key, by getValue. */
    @Benchmark
    public int check() {
        int equal = 0;
        for (int i = 0; i < SIZE; i++) {
            FunctionalKey k = keys[i];
            Glob g = globs[i];
            boolean ok = true;
            for (Field field : k.getBuilder().getFields()) {
                if (!field.valueEqual(k.getValue(field), g.getValue(field))) {
                    ok = false;
                    break;
                }
            }
            if (ok) equal++;
        }
        return equal;
    }

    /** The same check through FunctionalKey.matches, unrolled by the 3/4-field keys. */
    @Benchmark
    public int matches() {
        int equal = 0;
        for (int i = 0; i < SIZE; i++) {
            if (keys[i].matches(globs[i])) equal++;
        }
        return equal;
    }

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder().include(PerfFunctionalKey.class.getSimpleName()).build()).run();
    }
}
