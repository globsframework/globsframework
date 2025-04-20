package org.globsframework.core.utils;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.DummyObjectWithInner;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.GlobTypeResolver;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GlobTypeToGlobTest extends TestCase {

    public void testName() {
        final Collection<Glob> glob = GlobTypeToGlob.toGlob(DummyObject.TYPE);
        final Collection<GlobType> globTypes = GlobTypeToGlob.fromGlob(glob, GlobTypeResolver.ERROR);
        Assert.assertEquals(1, globTypes.size());
        final GlobType first = globTypes.iterator().next();
        Assert.assertEquals(DummyObject.TYPE.getName(), first.getName());
        Assert.assertEquals(9, first.getFieldCount());
        for (Field field : DummyObject.TYPE.getFields()) {
            final Field newF = first.getField(field.getName());
            final List<Glob> list = field.streamAnnotations().collect(Collectors.toList());
            for (Glob an : list) {
                Assert.assertTrue(newF.hasAnnotation(an.getKey()));
            }
        }
    }

    public void testWithSubObject() {
        final Collection<Glob> glob = GlobTypeToGlob.toGlob(DummyObjectWithInner.TYPE);
        final Collection<GlobType> globTypes = GlobTypeToGlob.fromGlob(glob, GlobTypeResolver.ERROR);
        Assert.assertEquals(3, globTypes.size());
        final GlobType first = globTypes.iterator().next();
        Assert.assertEquals(6, first.getFieldCount());
        first.getField(DummyObjectWithInner.VALUE_UNION.getName());
        first.getField(DummyObjectWithInner.VALUES_UNION.getName());
    }
}
