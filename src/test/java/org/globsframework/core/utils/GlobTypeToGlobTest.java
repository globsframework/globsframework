package org.globsframework.core.utils;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObjectWithInner;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeResolver;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobTypeToGlobTest {

    @Test
    public void testName() {
        final Collection<Glob> glob = GlobTypeToGlob.toGlob(DummyObject.TYPE);
        final Collection<GlobType> globTypes = GlobTypeToGlob.fromGlob(glob, GlobTypeResolver.ERROR);
        assertEquals(1, globTypes.size());
        final GlobType first = globTypes.iterator().next();
        assertEquals(DummyObject.TYPE.getName(), first.getName());
        assertEquals(9, first.getFieldCount());
        for (Field field : DummyObject.TYPE.getFields()) {
            final Field newF = first.getField(field.getName());
            final List<Glob> list = field.streamAnnotations().toList();
            for (Glob an : list) {
                assertTrue(newF.hasAnnotation(an.getKey()));
            }
        }
    }

    @Test
    public void testWithSubObject() {
        final Collection<Glob> glob = GlobTypeToGlob.toGlob(DummyObjectWithInner.TYPE);
        final Collection<GlobType> globTypes = GlobTypeToGlob.fromGlob(glob, GlobTypeResolver.ERROR);
        assertEquals(3, globTypes.size());
        final GlobType first = globTypes.iterator().next();
        assertEquals(6, first.getFieldCount());
        first.getField(DummyObjectWithInner.VALUE_UNION.getName());
        first.getField(DummyObjectWithInner.VALUES_UNION.getName());
    }
}
