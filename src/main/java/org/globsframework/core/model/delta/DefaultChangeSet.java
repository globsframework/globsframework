package org.globsframework.core.model.delta;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.*;
import org.globsframework.core.model.utils.BreakException;
import org.globsframework.core.model.utils.ChangeVisitor;
import org.globsframework.core.utils.collections.LinksMapOfMaps;
import org.globsframework.core.utils.collections.MapOfMaps;
import org.globsframework.core.utils.exceptions.InvalidState;

import java.io.IOException;
import java.util.*;

public class DefaultChangeSet implements MutableChangeSet {
    private MapOfMaps<GlobType, Key, DefaultDeltaGlob> deltaGlobsByKey;

    public DefaultChangeSet() {
        this(new LinksMapOfMaps<>());
    }

    private DefaultChangeSet(MapOfMaps<GlobType, Key, DefaultDeltaGlob> deltaGlobsByKey) {
        this.deltaGlobsByKey = deltaGlobsByKey;
    }

    static public DefaultChangeSet createOrdered() {
        return new DefaultChangeSet(new LinksMapOfMaps<>());
    }

    public void processCreation(Key key, FieldsValueScanner values) {
        DefaultDeltaGlob delta = getGlob(key);
        delta.processCreation(values);
        removeIfUnchanged(delta);
    }

    public void processCreation(GlobType type, FieldValues values) {
        processCreation(KeyBuilder.createFromValues(type, values), FieldValuesBuilder.removeKeyFields(values));
    }

    public void processUpdate(Key key, Field field, Object newValue, Object previousValue) {
        DefaultDeltaGlob delta = getGlob(key);
        delta.processUpdate(field, newValue, previousValue);
        removeIfUnchanged(delta);
    }

    public void processUpdate(Key key, FieldsValueWithPreviousScanner values) {
        final DefaultDeltaGlob delta = getGlob(key);
        FieldValuesWithPrevious.FunctorWithPrevious functor = new FieldValuesWithPrevious.FunctorWithPrevious() {
            public void process(Field field, Object value, Object previousValue) throws Exception {
                delta.processUpdate(field, value, previousValue);
                removeIfUnchanged(delta);
            }
        };
        values.safeApplyWithPrevious(functor.previousWithoutKey());
    }

    public void processDeletion(Key key, FieldsValueScanner values) {
        DefaultDeltaGlob delta = getGlob(key);
        delta.processDeletion(values);
        removeIfUnchanged(delta);
    }

    private void removeIfUnchanged(DefaultDeltaGlob delta) {
        if (!delta.isModified()) {
            deltaGlobsByKey.remove(delta.getKey().getGlobType(), delta.getKey());
        }
    }

    protected DefaultDeltaGlob getGlob(Key key) {
        DefaultDeltaGlob glob = deltaGlobsByKey.get(key.getGlobType(), key);
        if (glob == null) {
            glob = createDeltaGlob(key);
            deltaGlobsByKey.put(key.getGlobType(), key, glob);
        }
        return glob;
    }

    protected DefaultDeltaGlob createDeltaGlob(Key key) {
        return new DefaultDeltaGlob(key);
    }

    public void accept(ChangeSetVisitor visitor) throws Exception {
        Collection<DefaultDeltaGlob> values = deltaGlobsByKey.values();
        accept(visitor, values, DeltaState.DELETED);
        accept(visitor, values, DeltaState.UPDATED);
        accept(visitor, values, DeltaState.CREATED);
    }

    public void safeAccept(ChangeSetVisitor visitor) {
        try {
            accept(visitor);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void accept(GlobType type, ChangeSetVisitor visitor) throws Exception {
        Collection<DefaultDeltaGlob> values = deltaGlobsByKey.get(type).values();
        accept(visitor, values, DeltaState.DELETED);
        accept(visitor, values, DeltaState.UPDATED);
        accept(visitor, values, DeltaState.CREATED);
    }

    private void accept(ChangeSetVisitor visitor, Collection<DefaultDeltaGlob> globs, DeltaState state) throws Exception {
        for (DefaultDeltaGlob deltaGlob : globs) {
            if (deltaGlob.getState() == state) {
                deltaGlob.visit(visitor);
            }
        }
    }

    public void safeAccept(GlobType type, ChangeSetVisitor visitor) {
        try {
            accept(type, visitor);
        } catch (BreakException e) {
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void accept(Key key, ChangeSetVisitor visitor) throws Exception {
        DefaultDeltaGlob deltaGlob = deltaGlobsByKey.get(key.getGlobType(), key);
        if (deltaGlob != null) {
            deltaGlob.visit(visitor);
        }
    }

    public void safeAccept(Key key, ChangeSetVisitor visitor) {
        try {
            accept(key, visitor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsChanges(GlobType type) {
        return deltaGlobsByKey.contains(type) && !deltaGlobsByKey.get(type).isEmpty();
    }

    public GlobType[] getChangedTypes() {
        Set<GlobType> result = new HashSet<GlobType>();
        for (GlobType globType : deltaGlobsByKey.keys()) {
            if (!deltaGlobsByKey.get(globType).isEmpty()) {
                result.add(globType);
            }
        }
        return result.toArray(new GlobType[result.size()]);
    }

    public int getChangeCount() {
        return deltaGlobsByKey.size();
    }

    public int getChangeCount(GlobType type) {
        return deltaGlobsByKey.get(type).size();
    }

    public Set<Key> getChanged(GlobType type) {
        if (!deltaGlobsByKey.contains(type)) {
            return Collections.emptySet();
        }
        Set<Key> result = new HashSet<Key>();
        for (Map.Entry entry : deltaGlobsByKey.get(type).entrySet()) {
            result.add((Key) entry.getKey());
        }
        return result;
    }

    public Set<Key> getCreated(GlobType type) {
        if (!deltaGlobsByKey.contains(type)) {
            return Collections.emptySet();
        }
        Set<Key> result = new HashSet<Key>();
        for (Map.Entry entry : deltaGlobsByKey.get(type).entrySet()) {
            DefaultDeltaGlob delta = (DefaultDeltaGlob) entry.getValue();
            if (delta.isCreated()) {
                result.add((Key) entry.getKey());
            }
        }
        return result;
    }

    public Set<Key> getUpdated(GlobType type) {
        if (!deltaGlobsByKey.contains(type)) {
            return Collections.emptySet();
        }
        Set<Key> result = new HashSet<Key>();
        for (DefaultDeltaGlob delta : deltaGlobsByKey.get(type).values()) {
            if (delta.isUpdated()) {
                result.add(delta.getKey());
            }
        }
        return result;
    }

    public Set<Key> getCreatedOrUpdated(GlobType type) {
        if (!deltaGlobsByKey.contains(type)) {
            return Collections.emptySet();
        }
        Set<Key> result = new HashSet<Key>();
        for (DefaultDeltaGlob delta : deltaGlobsByKey.get(type).values()) {
            if (delta.isCreated() || delta.isUpdated()) {
                result.add(delta.getKey());
            }
        }
        return result;
    }

    public Set<Key> getUpdated(Field field) {
        if (!deltaGlobsByKey.contains(field.getGlobType())) {
            return Collections.emptySet();
        }
        Set<Key> result = new HashSet<Key>();
        for (DefaultDeltaGlob delta : deltaGlobsByKey.get(field.getGlobType()).values()) {
            if (delta.isUpdated(field)) {
                result.add(delta.getKey());
            }
        }
        return result;
    }

    public Set<Key> getDeleted(GlobType type) {
        if (!deltaGlobsByKey.contains(type)) {
            return Collections.emptySet();
        }
        Set<Key> result = new HashSet<Key>();
        for (DefaultDeltaGlob delta : deltaGlobsByKey.get(type).values()) {
            if (delta.isDeleted()) {
                result.add(delta.getKey());
            }
        }
        return result;
    }

    public boolean isCreated(Key key) {
        if (!deltaGlobsByKey.contains(key.getGlobType())) {
            return false;
        }
        DefaultDeltaGlob defaultDeltaGlob = deltaGlobsByKey.get(key.getGlobType(), key);
        return defaultDeltaGlob != null && defaultDeltaGlob.isCreated();
    }

    public boolean isDeleted(Key key) {
        if (!deltaGlobsByKey.contains(key.getGlobType())) {
            return false;
        }
        DefaultDeltaGlob defaultDeltaGlob = deltaGlobsByKey.get(key.getGlobType(), key);
        return defaultDeltaGlob != null && defaultDeltaGlob.isDeleted();
    }

    public FieldValues getPreviousValues(Key key) {
        Map<Key, DefaultDeltaGlob> keyDefaultDeltaGlobMap = deltaGlobsByKey.get(key.getGlobType());
        if (keyDefaultDeltaGlobMap != null) {
            DefaultDeltaGlob defaultDeltaGlob = keyDefaultDeltaGlobMap.get(key);
            return defaultDeltaGlob.getPreviousValues();
        }
        return null;
    }

    public FieldValues getNewValues(Key key) {
        Map<Key, DefaultDeltaGlob> keyDefaultDeltaGlobMap = deltaGlobsByKey.get(key.getGlobType());
        if (keyDefaultDeltaGlobMap != null) {
            DefaultDeltaGlob defaultDeltaGlob = keyDefaultDeltaGlobMap.get(key);
            return defaultDeltaGlob;
        }
        return null;
    }

    public boolean containsCreationsOrDeletions(GlobType type) {
        if (deltaGlobsByKey.contains(type)) {
            for (DefaultDeltaGlob deltaGlob : deltaGlobsByKey.get(type).values()) {
                if (deltaGlob.isCreated() || deltaGlob.isDeleted()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsCreations(GlobType type) {
        if (deltaGlobsByKey.contains(type)) {
            for (DefaultDeltaGlob deltaGlob : deltaGlobsByKey.get(type).values()) {
                if (deltaGlob.isCreated()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsDeletions(GlobType type) {
        if (deltaGlobsByKey.contains(type)) {
            for (DefaultDeltaGlob deltaGlob : deltaGlobsByKey.get(type).values()) {
                if (deltaGlob.isDeleted()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsUpdates(Field field) {
        GlobType type = field.getGlobType();
        if (deltaGlobsByKey.contains(type)) {
            for (DefaultDeltaGlob deltaGlob : deltaGlobsByKey.get(type).values()) {
                if (deltaGlob.isUpdated(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsChanges(Key key) {
        return deltaGlobsByKey.containsKey(key.getGlobType(), key);
    }

    public boolean containsChanges(Key key, Field... fields) {
        if (deltaGlobsByKey.contains(key.getGlobType())) {
            DefaultDeltaGlob glob = deltaGlobsByKey.get(key.getGlobType(), key);
            if (glob == null) {
                return false;
            }
            if (glob.isCreated() || glob.isDeleted()) {
                return true;
            }
            for (Field field : fields) {
                if (glob.isUpdated(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return deltaGlobsByKey.isEmpty();
    }

    public int size() {
        return deltaGlobsByKey.size();
    }

    public void merge(ChangeSet other) throws InvalidState {
        other.safeAccept(new ChangeSetVisitor() {
            public void visitCreation(Key key, FieldsValueScanner values) throws Exception {
                processCreation(key, values);
            }

            public void visitUpdate(final Key key, FieldsValueWithPreviousScanner values) throws Exception {
                FieldValuesWithPrevious.FunctorWithPrevious functor = new FieldValuesWithPrevious.FunctorWithPrevious() {
                    public void process(Field field, Object value, Object previousValue) throws IOException {
                        processUpdate(key, field, value, previousValue);
                    }
                };
                values.applyWithPrevious(functor.previousWithoutKey());
            }

            public void visitDeletion(Key key, FieldsValueScanner values) throws Exception {
                processDeletion(key, values);
            }
        });
    }

    public String toString() {
        try {
            StringBuilder writer = new StringBuilder();
            accept(new PrintChangeVisitor(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear(Collection<GlobType> globTypes) {
        for (GlobType type : globTypes) {
            deltaGlobsByKey.removeAll(type);
        }
    }

    public ChangeSet reverse() {
        DefaultChangeSet result = new DefaultChangeSet();
        for (DefaultDeltaGlob delta : deltaGlobsByKey.values()) {
            DefaultDeltaGlob reverseDelta = delta.reverse();
            result.deltaGlobsByKey.put(reverseDelta.getType(), reverseDelta.getKey(), reverseDelta);
        }
        return result;
    }

    private static class PrintChangeVisitor implements ChangeVisitor, FieldValues.Functor {
        private final StringBuilder writer;
        private String prefix = "";

        public PrintChangeVisitor(StringBuilder writer) {
            this.writer = writer;
        }

        public void complete() {
        }

        public void visitCreation(Key key, FieldsValueScanner values) throws Exception {
            writer.append("create :");
            key.safeApplyOnKeyField(this);
            values.safeApply(this.withoutKeyField());
            writer.append("\n");
        }

        public void visitUpdate(Key key, FieldsValueWithPreviousScanner values) throws Exception {
            int startOffSet = writer.length();
            writer.append("update :");
            key.safeApplyOnKeyField(this);
            int endOffset = writer.length();
            values.safeApply(this.withoutKeyField());
            writer.append("\n");
            for (int i = 0; i < endOffset - startOffSet; i++) {
                writer.append(" ");
            }
            prefix = "_";
            values.applyOnPrevious(this.withoutKeyField());
            writer.append("\n");
            prefix = "";
        }

        public void visitDeletion(Key key, FieldsValueScanner previousValues) throws Exception {
            writer.append("delete :");
            key.safeApplyOnKeyField(this);
            previousValues.safeApply(this.withoutKeyField());
            writer.append("\n");
        }

        public void process(Field field, Object value) throws Exception {
            writer.append(" ");
            writer.append(prefix);
            writer.append(field.getName());
            writer.append("='");
            if (value != null) {
                if (value instanceof String) {
                    writer.append(value);
                }
                else {
                    field.toString(writer, value);
                }
            }
            writer.append("'");
        }
    }
}
