package org.globsframework.core.model.delta;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeLoaderFactory;
import org.globsframework.core.metamodel.annotations.KeyField_;
import org.globsframework.core.metamodel.annotations.Target;
import org.globsframework.core.metamodel.fields.GlobArrayField;
import org.globsframework.core.metamodel.fields.GlobField;
import org.globsframework.core.metamodel.fields.IntegerField;
import org.globsframework.core.metamodel.fields.StringField;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Set;

public class InDepthChangeSetTest {

    @Test
    public void name() {
        DefaultChangeSet changeSet = new InDepthChangeSet();

        Glob sub1 = SubType.TYPE.instantiate().set(SubType.SUB_NAME, "nSub1").set(SubType.UUID, "AAAA");

        Glob subWOK2 = SubTypeWWithoutKey.TYPE.instantiate().set(SubTypeWWithoutKey.COUNT, 3)
                .set(SubTypeWWithoutKey.UUID, "KKKK");

        Glob d1 = DummyType.TYPE.instantiate().set(DummyType.NAME, "d1").set(DummyType.UUID, "XXXX")
                .set(DummyType.SUB_ELEMENT, sub1)
                .set(DummyType.COUNTS, new Glob[]{subWOK2});

        changeSet.processCreation(d1.getKey(), d1);

        Glob subD21 = SubType.TYPE.instantiate().set(SubType.SUB_NAME, "nSub1").set(SubType.UUID, "AAAAD21");
        Glob subD22 = SubType.TYPE.instantiate().set(SubType.SUB_NAME, "nSub1").set(SubType.UUID, "AAAAD22");
        Glob d2 = DummyType.TYPE.instantiate().set(DummyType.NAME, "d1").set(DummyType.UUID, "YYYYY");
        changeSet.processUpdate(d2.getKey(), DummyType.NAME, "d1", "d2");
        changeSet.processUpdate(d2.getKey(), DummyType.SUB_ELEMENT, subD21, subD22);

        Glob sub2 = SubType.TYPE.instantiate().set(SubType.SUB_NAME, "nSub1").set(SubType.UUID, "AAAABB");

        Glob d3 = DummyType.TYPE.instantiate()
                .set(DummyType.UUID, "ZZZZ").set(DummyType.NAME, "d3")
                .set(DummyType.SUB_ELEMENT, sub2);
        changeSet.processDeletion(d3.getKey(), d3);

        {
            Set<Key> created = changeSet.getCreated(SubType.TYPE);
            assertEquals(created.size(), 2);
            assertTrue(created.contains(sub1.getKey()));
            assertTrue(created.contains(subD21.getKey()));
        }
        {
            Set<Key> created = changeSet.getCreated(DummyType.TYPE);
            assertEquals(created.size(), 1);
            assertTrue(created.contains(d1.getKey()));
        }
        {
            Set<Key> created = changeSet.getCreated(SubTypeWWithoutKey.TYPE);
            assertEquals(created.size(), 1);
            assertTrue(created.contains(subWOK2.getKey()));
        }
        {
            Set<Key> deleted = changeSet.getDeleted(DummyType.TYPE);
            assertEquals(deleted.size(), 1);
            assertTrue(deleted.contains(d3.getKey()));
        }
        {
            Set<Key> deleted = changeSet.getDeleted(SubType.TYPE);
            assertEquals(deleted.size(), 2);
            assertTrue(deleted.contains(sub2.getKey()));
            assertTrue(deleted.contains(subD22.getKey()));
        }
        {
            Set<Key> updated = changeSet.getUpdated(DummyType.TYPE);
            assertEquals(updated.size(), 1);
            assertTrue(updated.contains(d2.getKey()));
        }
    }

    public static class DummyType {
        public static GlobType TYPE;

        @KeyField_
        public static StringField UUID;

        public static StringField NAME;

        @Target(SubType.class)
        public static GlobField SUB_ELEMENT;

        @Target(SubTypeWWithoutKey.class)
        public static GlobArrayField COUNTS;

        static {
            GlobTypeLoaderFactory.create(DummyType.class).load();
        }
    }

    public static class SubType {
        public static GlobType TYPE;

        @KeyField_
        public static StringField UUID;

        public static StringField SUB_NAME;

        static {
            GlobTypeLoaderFactory.create(SubType.class).load();
        }
    }

    //was without key
    public static class SubTypeWWithoutKey {
        public static GlobType TYPE;

        @KeyField_
        public static StringField UUID;

        public static IntegerField COUNT;

        static {
            GlobTypeLoaderFactory.create(SubTypeWWithoutKey.class).load();
        }
    }

}
