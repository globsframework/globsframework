package org.globsframework.core.model.repository;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.index.MultiFieldIndex;
import org.globsframework.core.metamodel.index.SingleFieldIndex;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.model.*;
import org.globsframework.core.model.delta.DefaultChangeSet;
import org.globsframework.core.model.utils.ChangeVisitor;
import org.globsframework.core.model.utils.GlobFunctor;
import org.globsframework.core.utils.Utils;
import org.globsframework.core.utils.collections.MultiMap;
import org.globsframework.core.utils.exceptions.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReplicationGlobRepository extends DefaultGlobRepository implements GlobRepository {
    private Set<GlobType> managedTypes = new HashSet<>();
    private GlobRepository originalRepository;
    private List<ChangeSetListener> triggers = new ArrayList<>();
    private List<ChangeSetListener> listeners = new ArrayList<>();
    private ChangeSetListener forwardChangeSetListener;

    public ReplicationGlobRepository(GlobRepository repository, GlobType... managedTypes) {
        super(repository.getIdGenerator());
        originalRepository = repository;
        this.managedTypes.addAll(Arrays.asList(managedTypes));
        forwardChangeSetListener = new ForwardChangeSetListener(this);
        repository.addChangeListener(forwardChangeSetListener);
    }

    public void startChangeSet() {
        super.startChangeSet();
        originalRepository.startChangeSet();
    }

    public void completeChangeSet() {
        originalRepository.completeChangeSet();
        super.completeChangeSet();
    }

    public void completeChangeSetWithoutTriggers() {
        originalRepository.completeChangeSetWithoutTriggers();
        super.completeChangeSetWithoutTriggers();
    }


    public Glob find(Key key) {
        if (key == null) {
            return null;
        }
        if (managedTypes.contains(key.getGlobType())) {
            return super.find(key);
        } else {
            return originalRepository.find(key);
        }
    }

    public Glob get(Key key) throws ItemNotFound {
        if (managedTypes.contains(key.getGlobType())) {
            return super.get(key);
        } else {
            return originalRepository.get(key);
        }
    }

    public Glob findUnique(GlobType type, FieldValue... values) throws ItemAmbiguity {
        if (managedTypes.contains(type)) {
            return super.findUnique(type, values);
        } else {
            return originalRepository.findUnique(type, values);
        }
    }

    public List<Glob> getAll(GlobType... type) {
        List<Glob> globs = super.getAll(type);
        globs.addAll(originalRepository.getAll(type));
        return globs;
    }

    public List<Glob> getAll(GlobType type, final Predicate<Glob> matcher) {
        if (managedTypes.contains(type)) {
            return super.getAll(type, matcher);
        } else {
            return originalRepository.getAll(type, matcher);
        }
    }

    public void apply(GlobType type, Predicate<Glob> matcher, GlobFunctor callback) throws Exception {
        if (managedTypes.contains(type)) {
            super.apply(type, matcher, callback);
        } else {
            originalRepository.apply(type, matcher, callback);
        }
    }

    public void safeApply(GlobFunctor callback) {
        super.safeApply(new GlobFunctor() {
            public void run(Glob glob, GlobRepository repository) throws Exception {
                callback.run(glob, ReplicationGlobRepository.this);
            }
        });
        originalRepository.safeApply(new GlobFunctor() {
            public void run(Glob glob, GlobRepository repository) throws Exception {
                if (!managedTypes.contains(glob.getType())) {
                    callback.run(glob, ReplicationGlobRepository.this);
                }
            }
        });
    }

    public void safeApply(GlobType type, Predicate<Glob> matcher, GlobFunctor callback) {
        if (managedTypes.contains(type)) {
            super.safeApply(type, matcher, callback);
        } else {
            originalRepository.safeApply(type, matcher, callback);
        }
    }

    public Glob findUnique(GlobType type, Predicate<Glob> matcher) throws ItemAmbiguity {
        if (managedTypes.contains(type)) {
            return super.findUnique(type, matcher);
        } else {
            return originalRepository.findUnique(type, matcher);
        }
    }

    public Glob[] getSorted(GlobType type, Comparator<Glob> comparator, Predicate<Glob> matcher) {

        if (managedTypes.contains(type)) {
            return super.getSorted(type, comparator, matcher);
        } else {
            return originalRepository.getSorted(type, comparator, matcher);
        }
    }

    public List<Glob> findByIndex(SingleFieldIndex index, Object value) {
        if (managedTypes.contains(index.getField().getGlobType())) {
            return super.findByIndex(index, value);
        } else {
            return originalRepository.findByIndex(index, value);
        }
    }

    public MultiFieldIndexed findByIndex(MultiFieldIndex multiFieldIndex, Field field, Object value) {
        if (managedTypes.contains(field.getGlobType())) {
            return super.findByIndex(multiFieldIndex, field, value);
        } else {
            return originalRepository.findByIndex(multiFieldIndex, field, value);
        }
    }

    public Set<GlobType> getTypes() {
        HashSet<GlobType> types = new HashSet<GlobType>();
        types.addAll(this.managedTypes);
        types.addAll(originalRepository.getTypes());
        return types;
    }

    public Glob findLinkTarget(Glob source, Link link) {
        if (managedTypes.contains(link.getTargetType())) {
            return super.findLinkTarget(source, link);
        } else {
            return originalRepository.findLinkTarget(source, link);
        }
    }

    public List<Glob> findLinkedTo(Key target, Link link) {
        if (managedTypes.contains(link.getSourceType())) {
            return super.findLinkedTo(target, link);
        } else {
            return originalRepository.findLinkedTo(target, link);
        }
    }

    public List<Glob> findLinkedTo(Glob target, Link link) {
        if (managedTypes.contains(link.getSourceType())) {
            return super.findLinkedTo(target, link);
        } else {
            return originalRepository.findLinkedTo(target, link);
        }
    }

    public Glob create(GlobType type, FieldValue... values) throws MissingInfo, ItemAlreadyExists {
        if (managedTypes.contains(type)) {
            return super.create(type, values);
        } else {
            return originalRepository.create(type, values);
        }
    }

    public Glob create(Key key, FieldValue... values) throws ItemAlreadyExists {
        if (managedTypes.contains(key.getGlobType())) {
            return super.create(key, values);
        } else {
            return originalRepository.create(key, values);
        }
    }

    public Glob findOrCreate(Key key, FieldValue... valuesForCreate) throws MissingInfo {
        if (managedTypes.contains(key.getGlobType())) {
            return super.findOrCreate(key, valuesForCreate);
        } else {
            return originalRepository.findOrCreate(key, valuesForCreate);
        }
    }

    public boolean contains(GlobType type) {
        return super.contains(type) || originalRepository.contains(type);
    }

    public boolean contains(GlobType type, Predicate<Glob> matcher) {
        return super.contains(type, matcher) || originalRepository.contains(type, matcher);
    }

    public void update(Key key, Field field, Object newValue) throws ItemNotFound {
        if (managedTypes.contains(key.getGlobType())) {
            super.update(key, field, newValue);
        } else {
            originalRepository.update(key, field, newValue);
        }
    }

    public void update(Key key, FieldValue... values) {
        if (managedTypes.contains(key.getGlobType())) {
            super.update(key, values);
        } else {
            originalRepository.update(key, values);
        }
    }

    public void setTarget(Key source, Link link, Key target) throws ItemNotFound {
        if (managedTypes.contains(source.getGlobType())) {
            super.setTarget(source, link, target);
        } else {
            originalRepository.setTarget(source, link, target);
        }
    }

    public void delete(Key key) throws ItemNotFound, OperationDenied {
        if (managedTypes.contains(key.getGlobType())) {
            super.delete(key);
        } else {
            originalRepository.delete(key);
        }
    }

    public void delete(Collection<Key> keys) throws ItemNotFound, OperationDenied {
        MultiMap<GlobType, Key> map = new MultiMap<GlobType, Key>();
        for (Key key : keys) {
            map.put(key.getGlobType(), key);
        }
        for (GlobType type : map.keySet()) {
            if (managedTypes.contains(type)) {
                super.delete(map.get(type));
            } else {
                originalRepository.delete(map.get(type));
            }
        }
    }

    public void deleteGlobs(Collection<Glob> list) throws OperationDenied {
        List<Glob> localDelete = list.stream()
                .filter(glob -> managedTypes.contains(glob.getType())).collect(Collectors.toList());
        super.deleteGlobs(localDelete);
        List<Glob> remoteDelete = list.stream().filter(glob -> !managedTypes.contains(glob.getType())).collect(Collectors.toList());
        originalRepository.deleteGlobs(remoteDelete);
    }

    public void deleteAll(GlobType... types) throws OperationDenied {
        for (GlobType type : types) {
            if (this.managedTypes.contains(type)) {
                super.deleteAll(type);
            } else {
                originalRepository.deleteAll(types);
            }
        }
    }

    public void apply(ChangeSet changeSet) throws InvalidParameter {
        final DefaultChangeSet localChangeSet = new DefaultChangeSet();
        final DefaultChangeSet remoteChangeSet = new DefaultChangeSet();
        changeSet.safeAccept(new ChangeVisitor() {
            public void complete() {
            }

            public void visitCreation(Key key, FieldsValueScanner values) throws Exception {
                if (managedTypes.contains(key.getGlobType())) {
                    localChangeSet.processCreation(key, values);
                } else {
                    remoteChangeSet.processCreation(key, values);
                }
            }

            public void visitUpdate(Key key, FieldsValueWithPreviousScanner values) throws Exception {
                if (managedTypes.contains(key.getGlobType())) {
                    localChangeSet.processUpdate(key, values);
                } else {
                    remoteChangeSet.processUpdate(key, values);
                }
            }

            public void visitDeletion(Key key, FieldsValueScanner previousValues) throws Exception {
                if (managedTypes.contains(key.getGlobType())) {
                    localChangeSet.processDeletion(key, previousValues);
                } else {
                    remoteChangeSet.processDeletion(key, previousValues);
                }
            }
        });
        super.apply(localChangeSet);
        originalRepository.apply(remoteChangeSet);
    }

    public void addTrigger(ChangeSetListener listener) {
        triggers.add(listener);
        super.addTrigger(listener);
    }

    public void addTriggerAtFirst(ChangeSetListener trigger) {
        triggers.add(0, trigger);
        super.addTriggerAtFirst(trigger);
    }

    public void removeTrigger(ChangeSetListener listener) {
        triggers.remove(listener);
        super.removeTrigger(listener);
    }

    public void addChangeListener(ChangeSetListener listener) {
        listeners = new ArrayList<ChangeSetListener>(listeners);
        listeners.add(listener);
        super.addChangeListener(listener);
//    orgRepository.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeSetListener listener) {
        super.removeChangeListener(listener);
        listeners = new ArrayList<ChangeSetListener>(listeners);
        boolean isRemoved = listeners.remove(listener);
        Utils.beginRemove();
        if (!isRemoved) {
            throw new RuntimeException("BUG");
        }
        Utils.endRemove();
//    orgRepository.removeChangeListener(listener);
    }

    public void reset(Collection<Glob> newGlobs, GlobType... changedTypes) {
        List<Glob> localList = new ArrayList<>();
        List<Glob> remoteList = new ArrayList<>();
        for (Glob glob : newGlobs) {
            if (managedTypes.contains(glob.getType())) {
                localList.add(glob);
            } else {
                remoteList.add(glob);
            }
        }
        List<GlobType> localTypes = new ArrayList<>();
        List<GlobType> remoteTypes = new ArrayList<>();
        for (GlobType type : changedTypes) {
            if (managedTypes.contains(type)) {
                localTypes.add(type);
            } else {
                remoteTypes.add(type);
            }
        }
        super.reset(localList, localTypes.toArray(new GlobType[0]));
        originalRepository.reset(remoteList, remoteTypes.toArray(new GlobType[0]));
    }

    public GlobIdGenerator getIdGenerator() {
        return super.getIdGenerator();
    }

    private class DecoratedGlobMatcher implements Predicate<Glob> {
        private final Predicate<Glob> matcher;

        public DecoratedGlobMatcher(Predicate<Glob> matcher) {
            this.matcher = matcher;
        }

        public boolean test(Glob item) {
            return matcher.test(item);
        }
    }

    private static class ForwardChangeSetListener implements ChangeSetListener {
        private ReplicationGlobRepository repository;

        public ForwardChangeSetListener(ReplicationGlobRepository repository) {
            this.repository = repository;
        }

        public void globsChanged(ChangeSet changeSet, GlobRepository repository) {
            this.repository.startChangeSet();
            for (ChangeSetListener trigger : this.repository.triggers) {
                trigger.globsChanged(changeSet, repository);
            }
            try {
                for (ChangeSetListener listener : this.repository.listeners) {
                    listener.globsChanged(changeSet, this.repository);
                }
            } finally {
                this.repository.completeChangeSet();
            }
        }

        public void globsReset(GlobRepository repository, Set<GlobType> changedTypes) {
            this.repository.startChangeSet();
            try {
                for (ChangeSetListener trigger : this.repository.triggers) {
                    trigger.globsReset(repository, changedTypes);
                }
                for (ChangeSetListener listener : this.repository.listeners) {
                    listener.globsReset(this.repository, changedTypes);
                }
            } finally {
                this.repository.completeChangeSet();
            }
        }
    }
}
