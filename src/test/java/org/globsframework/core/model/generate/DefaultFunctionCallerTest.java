package org.globsframework.core.model.generate;

import org.globsframework.core.metamodel.DummyObject;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.MutableGlob;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The fallback caller, and what a generated one has to agree with : every field is called, in index order,
 * with isSet from the Glob and isNull meaning "getValue answers null" — so an untouched field comes out
 * not set, null, and with no value, while an explicit null is set and null.
 */
public class DefaultFunctionCallerTest {

    private record Seen(String field, boolean isSet, boolean isNull, Object value, Object ctx2) {
    }

    private static GenerateCaller.GetFieldValueFunction<List<Seen>, String> recorder() {
        return new GenerateCaller.GetFieldValueFunction<>() {
            public <T> FieldValueFunction<T, List<Seen>, String> create(Field field) {
                String name = field.getName();
                return (isSet, isNull, value, ctx1, ctx2) -> ctx1.add(new Seen(name, isSet, isNull, value, ctx2));
            }
        };
    }

    private List<Seen> call(MutableGlob glob) {
        List<Seen> seen = new ArrayList<>();
        GenerateCaller.callerFor(glob.getType(), recorder()).call(glob, seen, "ctx2");
        return seen;
    }

    @Test
    public void everyFieldIsCalledInIndexOrder() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        List<Seen> seen = call(glob);
        assertEquals(
                Arrays.stream(DummyObject.TYPE.getFields()).map(Field::getName).collect(Collectors.toList()),
                seen.stream().map(Seen::field).collect(Collectors.toList()));
    }

    @Test
    public void aValueAnExplicitNullAndAnUntouchedFieldAreThreeDifferentThings() {
        MutableGlob glob = DummyObject.TYPE.instantiate();
        glob.set(DummyObject.NAME, "a name");
        glob.setValue(DummyObject.COUNT, null);

        List<Seen> seen = call(glob);
        assertEquals(new Seen("name", true, false, "a name", "ctx2"), of(seen, "name"));
        assertEquals(new Seen("count", true, true, null, "ctx2"), of(seen, "count"));
        assertEquals(new Seen("value", false, true, null, "ctx2"), of(seen, "value"));
    }

    @Test
    public void aTypeWithNoGeneratingFactoryFallsBackToTheLoopedCaller() {
        assertFalse(DummyObject.TYPE.getGlobFactory() instanceof GlobGenerateFactory);
        assertInstanceOf(DefaultFunctionCaller.class,
                GenerateCaller.callerFor(DummyObject.TYPE, recorder()));
    }

    @Test
    public void aMissingFunctionIsRefusedAtBuildTimeRatherThanNPEingPerGlob() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultFunctionCaller<>(DummyObject.TYPE,
                new GenerateCaller.GetFieldValueFunction<Object, Object>() {
                    public <T> FieldValueFunction<T, Object, Object> create(Field field) {
                        return null;
                    }
                }));
    }

    private Seen of(List<Seen> seen, String field) {
        return seen.stream().filter(s -> s.field().equals(field)).findFirst()
                .orElseThrow(() -> new AssertionError(field + " was not called"));
    }
}
