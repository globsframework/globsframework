package org.globsframework.core.model.cache;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.MutableGlob;
import org.globsframework.core.model.ReservationException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultGlobsCache implements GlobsCache {
    private final int maxGlobsInCache;
    private final Map<GlobType, Deque<MutableGlob>> globs = new ConcurrentHashMap<>();

    public DefaultGlobsCache(int maxGlobsInCache) {
        this.maxGlobsInCache = maxGlobsInCache;
    }

    public MutableGlob newGlob(GlobType globType, int id) {
        final Deque<MutableGlob> compute = globs.computeIfAbsent(globType, (t) -> new ArrayDeque<>());
        final MutableGlob data;
        synchronized (compute) {
            data = compute.poll();
        }
        if (data == null) {
            final MutableGlob instantiate = globType.instantiate();
            instantiate.reserve(id);
            return instantiate;
        } else {
            data.reserve(id);
            return data;
        }
    }

    public void release(Glob glob, int id) {
        if (glob.isReservedBy(id)) {
            if (glob instanceof MutableGlob mutableGlob) {
                final GlobType type = glob.getType();
                for (Field field : type.getFields()) {
                    Object value = mutableGlob.getValue(field);
                    mutableGlob.unset(field);
                    if (value != null) {
                        switch (field) {
                            case GlobField f -> release((Glob) value, id);
                            case GlobUnionField f -> release((Glob) value, id);
                            case GlobArrayField f -> {
                                for (Glob d : ((Glob[]) value)) {
                                    if (d != null) {
                                        release(d, id);
                                    }
                                }
                            }
                            case GlobArrayUnionField f -> {
                                for (Glob d : ((Glob[]) value)) {
                                    if (d != null) {
                                        release(d, id);
                                    }
                                }
                            }
                            default -> {
                            }
                        }
                    }
                }
                if (glob.release(id)) {
                    final Deque<MutableGlob> cache = globs.get(type);
                    if (cache == null) {
                        return;
                    }
                    synchronized (cache) {
                        if (cache.size() < maxGlobsInCache) {
                            cache.add(mutableGlob);
                        }
                    }
                }
            }
        } else {
            glob.checkWasReservedBy(id);
        }
    }
}
