package org.globsframework.core.metamodel.links;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;

// introduce typeded link => containmentLink, 1->1, 1->N, M->N, ... hierarchical.
public interface Link {

    GlobType getSourceType();

    GlobType getTargetType();

    String getLinkModelName();

    String getName();

    boolean isRequired();

    <T extends FieldMappingFunction> T apply(T functor);

    boolean isContainment();

    Key getTargetKey(Glob source);
}
