package org.globsframework.core.metamodel;

import org.globsframework.core.metamodel.fields.GlobField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.junit.jupiter.api.Assertions;

import java.util.function.Supplier;

public class NodeType {

    public static class Node1 {
        public static final GlobType TYPE;

        public static final IntegerField ID;

        public static final GlobField NODE;

        static {
            GlobTypeBuilder globTypeBuilder = DefaultGlobTypeBuilder.init("Node1");
            ID = globTypeBuilder.declareIntegerField("ID");
            NODE = globTypeBuilder.declareGlobField("node", () -> Node2.TYPE);
            TYPE = globTypeBuilder.build();
        }
    }

    public static class Node2 {
        public static final GlobType TYPE;

        public static final IntegerField ID;

        public static final GlobField NODE;

        static {
            GlobTypeBuilder globTypeBuilder = DefaultGlobTypeBuilder.init("Node2");
            ID = globTypeBuilder.declareIntegerField("ID");
            NODE = globTypeBuilder.declareGlobField("node", () -> Node1.TYPE);
            TYPE = globTypeBuilder.build();
        }
    }

    static void main() {
        Assertions.assertEquals(NodeType.Node1.NODE.getTargetType(), NodeType.Node2.TYPE);
        Assertions.assertEquals(NodeType.Node2.NODE.getTargetType(), NodeType.Node1.TYPE);
    }

}
