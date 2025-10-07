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
import java.util.function.Function;

public class DefaultGlobsCache implements GlobsCache {
    private static int INITIAL_SIZE = Integer.getInteger("globs.cache.initialSize", 128);
    private final int maxGlobsInCache;
    private final Map<GlobType, Deque<MutableGlob>> globs;

    public DefaultGlobsCache(int maxGlobsInCache) {
        this(maxGlobsInCache, INITIAL_SIZE);
    }

    public DefaultGlobsCache(int maxGlobsInCache, int initialCacheSize) {
        this.maxGlobsInCache = maxGlobsInCache;
        globs = new ConcurrentHashMap<>(initialCacheSize);
    }

    static Function<GlobType, Deque<MutableGlob>> f = (t) -> new ArrayDeque<>();

    public MutableGlob newGlob(GlobType globType, int id) {
        final Deque<MutableGlob> compute = globs.computeIfAbsent(globType, f);
        MutableGlob data;
        synchronized (compute) {
            data = compute.poll();
        }
        if (data == null) {
            data = globType.instantiate();
        }
        data.reserve(id);
        return data;
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
