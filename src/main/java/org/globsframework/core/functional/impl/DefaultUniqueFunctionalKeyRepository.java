package org.globsframework.core.functional.impl;

import org.globsframework.core.functional.FunctionalKey;
import org.globsframework.core.functional.FunctionalKeyBuilder;
import org.globsframework.core.functional.MutableFunctionalKeyRepository;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.impl.DefaultGlobTypeBuilder;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.format.GlobPrinter;
import org.globsframework.core.utils.collections.ConcurrentMapOfMaps;
import org.globsframework.core.utils.collections.MapOfMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class DefaultUniqueFunctionalKeyRepository implements MutableFunctionalKeyRepository {
    static private final Logger LOGGER = LoggerFactory.getLogger(DefaultUniqueFunctionalKeyRepository.class);
    private static final Glob NULL = DefaultGlobTypeBuilder.init("NULL GLOB").build().instantiate();
    private final DataAccess dataAccess;
    private final Map<FunctionalKey, Glob> index = new ConcurrentHashMap<>();
    private final MapOfMaps<GlobType, FunctionalKeyBuilder, Boolean> indexed = new ConcurrentMapOfMaps<>();

    public DefaultUniqueFunctionalKeyRepository(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public DefaultUniqueFunctionalKeyRepository(DataAccess dataAccess, Collection<FunctionalKeyBuilder> functionalKeyBuilders) {
        this.dataAccess = dataAccess;
        for (FunctionalKeyBuilder functionalKeyBuilder : functionalKeyBuilders) {
            indexed.put(functionalKeyBuilder.getType(), functionalKeyBuilder, Boolean.FALSE);
        }
    }

    public Collection<Glob> get(FunctionalKey functionalKey) {
        Glob glob = getUnique(functionalKey);
        if (glob == null) {
            return updateIndex(functionalKey);
        } else {
            return Collections.singletonList(glob);
        }
    }

    public Glob computeUniqueIfAbsent(FunctionalKey functionalKey, Function<FunctionalKey, Glob> mappingFunction) {
        Glob glob = index.get(functionalKey);
        if (glob != null) {
            if (glob == NULL) {
                return null;
            }
            return glob;
        }
        glob = mappingFunction.apply(functionalKey);
        if (glob == null) {
            index.put(functionalKey, NULL);
        } else {
            index.put(functionalKey, glob);
        }
        return glob;
    }

    public Stream<FunctionalKey> getAll(FunctionalKeyBuilder builder) {
        GlobType type = builder.getType();
        Map<FunctionalKeyBuilder, Boolean> functionalKeyBuilders = indexed.get(type);
        if (functionalKeyBuilders == null || functionalKeyBuilders.get(builder) != Boolean.TRUE) {
            synchronized (type) {
                dataAccess.all(type)
                        .forEach(g -> {
                            FunctionalKey key = builder.create(g);
                            update(g, key);
                        });
                indexed.put(type, builder, Boolean.TRUE);
            }
        }
        return index.keySet().stream().filter(functionalKey -> functionalKey.getBuilder() == builder);
    }

    public Glob getUnique(FunctionalKey functionalKey) {
        Glob glob = index.get(functionalKey);
        if (glob != null) {
            if (glob == NULL) {
                return null;
            }
            return glob;
        }
        updateIndex(functionalKey);
        return index.get(functionalKey);
    }

    private List<Glob> updateIndex(FunctionalKey functionalKey) {
        FunctionalKeyBuilder builder = functionalKey.getBuilder();
        GlobType type = builder.getType();
        Map<FunctionalKeyBuilder, Boolean> functionalKeyBuilders = indexed.get(type);
        if (functionalKeyBuilders != null && functionalKeyBuilders.get(builder) == Boolean.TRUE) {
            return Collections.emptyList();
        }
        synchronized (type) {
            dataAccess.all(type)
                    .forEach(g -> {
                        FunctionalKey key = builder.create(g);
                        update(g, key);
                    });
            indexed.put(type, builder, Boolean.TRUE);
            Glob o = index.get(functionalKey);
            return o != null ? Collections.singletonList(o) : Collections.emptyList();
        }
    }

    private void update(Glob g, FunctionalKey key) {
        Glob put = index.put(key, g);
        if (put != null) {
            LOGGER.error("Duplicate entry " + GlobPrinter.toString(put) + " and " + GlobPrinter.toString(g) + " for key " + GlobPrinter.toString(key));
        }
    }

    public void put(Glob glob) {
        synchronized (glob.getType()) {
            Map<FunctionalKeyBuilder, Boolean> functionalKeyBuilderBooleanMap = indexed.get(glob.getType());
            for (Map.Entry<FunctionalKeyBuilder, Boolean> functionalKeyBuilderBooleanEntry : functionalKeyBuilderBooleanMap.entrySet()) {
                update(glob, functionalKeyBuilderBooleanEntry.getKey().create(glob));
            }
        }
    }

    public void setNull(FunctionalKey oppositeKey) {
        index.put(oppositeKey, NULL);
    }

}
