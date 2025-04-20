package org.globsframework.core.model.indexing.indices;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.GlobRepository;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.model.utils.GlobFunctor;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;

import java.util.*;
import java.util.stream.Stream;

public class UniqueLeafLevelIndex implements UpdatableMultiFieldIndex, GlobRepository.MultiFieldIndexed {
    private Map<Object, Glob> indexedGlob = new HashMap<Object, Glob>();
    private Field field;

    public UniqueLeafLevelIndex(Field field) {
        this.field = field;
    }

    public List<Glob> getGlobs() {
        return new ArrayList<>(indexedGlob.values());
    }

    public Stream<Glob> streamByIndex(Object value) {
        Glob glob = indexedGlob.get(value);
        return glob == null ? Stream.empty() : Stream.of(glob);
    }

    public void saveApply(GlobFunctor functor, GlobRepository repository) {
        try {
            for (Glob glob : indexedGlob.values()) {
                functor.run(glob, repository);
            }
        } catch (Exception e) {
            throw new UnexpectedApplicationState(e);
        }
    }

    public List<Glob> findByIndex(Object value) {
        Glob glob = indexedGlob.get(value);
        if (glob == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(Collections.singletonList(glob));
        }
    }

    public boolean remove(Glob glob) {
        Object oldValue = glob.getValue(this.field);
        Glob oldGlob = indexedGlob.remove(oldValue);
        if (oldGlob != null && oldGlob != glob) {
            indexedGlob.put(oldValue, oldGlob);
        }
        return indexedGlob.isEmpty();
    }

    public void removeAll() {
        indexedGlob.clear();
    }

    public GlobRepository.MultiFieldIndexed findByIndex(Field field, final Object value) {
        return new GlobRepository.MultiFieldIndexed() {
            public List<Glob> getGlobs() {
                return UniqueLeafLevelIndex.this.findByIndex(value);
            }

            public List<Glob> findByIndex(Object value) {
                return new ArrayList<>();
            }

            public GlobRepository.MultiFieldIndexed findByIndex(Field field, Object value) {
                return null;
            }

            public void saveApply(GlobFunctor functor, GlobRepository repository) {
                try {
                    Glob glob = indexedGlob.get(value);
                    if (glob != null) {
                        functor.run(glob, repository);
                    }
                } catch (Exception e) {
                    throw new UnexpectedApplicationState(e);
                }
            }
        };
    }

    public void add(Field field, Object newValue, Object oldValue, Glob glob) {
        if (this.field == field) {
            Glob oldGlob = indexedGlob.remove(oldValue);
            if (oldGlob != null && oldGlob != glob) {
                indexedGlob.put(oldValue, oldGlob);
            }
            indexedGlob.put(newValue, glob);
        } else {
            indexedGlob.put(glob.getValue(this.field), glob);
        }
    }

    public boolean remove(Field field, Object value, Glob glob) {
        if (this.field == field) {
            Glob oldGlob = indexedGlob.remove(value);
            if (oldGlob != null && oldGlob != glob) {
                indexedGlob.put(value, oldGlob);
            }
            return indexedGlob.isEmpty();
        } else {
            return remove(glob);
        }
    }

    public void add(Glob glob) {
        Object value = glob.getValue(this.field);
        Glob oldGlob = indexedGlob.put(value, glob);
        if (oldGlob != null) {
            throw new RuntimeException("Should be an unique index\n" +
                    "- field: " + field.getName() + "\n" +
                    "- type: " + field.getGlobType() + "\n" +
                    "- new: \n " +
                    GlobPrinter.toString(glob) + "\n" +
                    "- old: \n " +
                    GlobPrinter.toString(oldGlob));
        }
    }
}
