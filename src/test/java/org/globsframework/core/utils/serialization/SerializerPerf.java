package org.globsframework.core.utils.serialization;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeBuilder;
import org.globsframework.core.metamodel.GlobTypeBuilderFactory;
import org.globsframework.core.metamodel.fields.DoubleField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.*;

/*
SerializerPerf.kryoSerialization  thrpt    4  17610255.662 ± 717297.099  ops/s

SerializerPerf.globSerialization  thrpt    4  20433136.709 ± 1282358.244  ops/s

SerializerPerf.globSerialization  thrpt    4  19825251.414 ± 1616578.957  ops/s ==> stableValue

 */

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 2, time = 3)
@Fork(2)
@State(Scope.Thread)
public class SerializerPerf {

    private DefaultBufferedSerializationOutput serializationOutput;
    private GlobSerializer globSerializer;
    private Glob data;
    private Kryo kryo;
    private TypicalBean bean;
    private Output output;

    @Setup
    public void setUp() {
        serializationOutput = new DefaultBufferedSerializationOutput(new byte[1024 * 1024]);
        globSerializer = new GlobSerializer(serializationOutput);
        data = NoTypicalBeanType.TYPE.instantiate()
                .set(NoTypicalBeanType.fistName, "Marcel")
                .set(NoTypicalBeanType.lastName, "Dupond")
                .set(NoTypicalBeanType.age, 45)
                .set(NoTypicalBeanType.height, 3.3);

        kryo = new Kryo();
        kryo.register(TypicalBean.class);

        bean = new TypicalBean();
        bean.firstName = "Marcel";
        bean.lastName = "Dupond";
        bean.age = 45;
        bean.height = 3.3;

        output = new Output(new byte[1024 * 1024]);
    }

    @Benchmark
    public void globSerialization() {
        serializationOutput.reset();
        globSerializer.writeKnowGlob(data);
        if (serializationOutput.position() != 34) {
            Assertions.fail("position is " + serializationOutput.position());
        }
    }

    @Benchmark
    public void kryoSerialization() {
        kryo.writeObject(output, bean);
        if (output.position() != 23) {
            Assertions.fail("position is " + output.position());
        }
        output.reset();
    }


    public static class NoTypicalBeanType {
        public static GlobType TYPE;

        public static final StringField fistName;

        public static final StringField lastName;

        public static final IntegerField age;

        public static final DoubleField height;

        static {
            final GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("NnTypicalBean");
            TYPE = globTypeBuilder.unCompleteType();
            fistName = globTypeBuilder.declareStringField("fistName");
            lastName = globTypeBuilder.declareStringField("lastName");
            age = globTypeBuilder.declareIntegerField("age");
            height = globTypeBuilder.declareDoubleField("height");
            globTypeBuilder.complete();
        }
    }

    static public class TypicalBean {
        private String firstName;
        private String lastName;
        private Integer age;
        private Double height;
    }
}
