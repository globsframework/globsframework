package org.globsframework.core.model.impl;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.openjdk.jmh.annotations.*;


/*
-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly
-XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*PerfGetIndex.globSet
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@Fork(2)
@State(Scope.Thread)
public class PerfGetIndex {
    private Glob data;

    @Setup
    public void setUp() {
        data = NoTypicalBeanType.TYPE.instantiate()
                .set(NoTypicalBeanType.fistName, "Marcel")
                .set(NoTypicalBeanType.lastName, "Dupond")
                .set(NoTypicalBeanType.age, 45)
                .set(NoTypicalBeanType.height, 3.3);

    }

    @Benchmark
    public Double globSet() {
        return data.get(NoTypicalBeanType.height) + data.get(NoTypicalBeanType.age);
    }

//    @Benchmark
//    public void kryoSerialization() {
//        kryo.writeObject(output, bean);
//        if (output.position() != 23) {
//            Assertions.fail("position is " + output.position());
//        }
//        output.reset();
//    }


    public static class NoTypicalBeanType {
        public static GlobType TYPE;

        public static final StringField fistName;

        public static final StringField lastName;

        public static final IntegerField age;

        public static final DoubleField height;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("NnTypicalBean");
            fistName = globTypeBuilder.declareStringField("fistName");
            lastName = globTypeBuilder.declareStringField("lastName");
            age = globTypeBuilder.declareIntegerField("age");
            height = globTypeBuilder.declareDoubleField("height");
            TYPE = globTypeBuilder.build();
        }
    }

    static public class TypicalBean {
        private String firstName;
        private String lastName;
        private Integer age;
        private Double height;
    }
}
