package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.Annotations;
import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.MutableGlobLinkModel;
import org.globsframework.core.metamodel.annotations.FieldName;
import org.globsframework.core.metamodel.annotations.LinkModelName;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.metamodel.impl.DefaultAnnotations;
import org.globsframework.core.metamodel.links.DirectLink;
import org.globsframework.core.metamodel.links.FieldMappingFunction;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.metamodel.utils.MutableAnnotations;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.Key;
import org.globsframework.core.utils.collections.MapOfMaps;
import org.globsframework.core.utils.exceptions.GlobsException;

import java.util.*;
import java.util.stream.Stream;

public class DefaultMutableGlobLinkModel implements MutableGlobLinkModel {
    public static final Link[] EMPTY = new Link[0];
    private final Map<GlobType, Link[]> links = new HashMap<>();
    private final Map<GlobType, Link[]> inboundLinks = new HashMap<>();
    private final MapOfMaps<GlobType, String, Link> outputLinkByName = new MapOfMaps<>();

    public DefaultMutableGlobLinkModel(GlobModel model) {
        for (GlobType type : model) {
            LinkRegister registered = type.getRegistered(LinkRegister.class);
            if (registered != null) {
                registered.register(this);
            }
        }
    }

    public Link[] getLinks(GlobType globType) {
        Link[] retLinks = links.get(globType);
        return retLinks == null ? EMPTY : retLinks;
    }

    public Link[] getInboundLinks(GlobType type) {
        Link[] retLinks = inboundLinks.get(type);
        return retLinks == null ? EMPTY : retLinks;
    }

    public Link getLink(GlobType type, String fieldName) {
        return outputLinkByName.get(type, fieldName);
    }

    public LinkBuilder getLinkBuilder(String modelName, String name, Glob... globAnnotations) {
        return getDirectLinkBuilder(modelName, name, globAnnotations);
    }


    interface OnPublish {
        void publish(Link link);
    }

    public DirectLinkBuilder getDirectLinkBuilder(String modelName, String name, Glob... globAnnotations) {
        return new DefaultDirectLinkBuilder(modelName, name,
                (link) -> {
                    appendInSource(link);
                    appendInTarget(link);
                }, new DefaultAnnotations(globAnnotations));
    }

    private void appendInSource(Link link) {
        Link[] current = links.get(link.getSourceType());
        outputLinkByName.put(link.getSourceType(), link.getName(), link);
        current = current == null ? new Link[1] : Arrays.copyOf(current, current.length + 1);
        current[current.length - 1] = link;
        links.put(link.getSourceType(), current);
    }

    private void appendInTarget(Link link) {
        Link[] current = inboundLinks.get(link.getTargetType());
        current = current == null ? new Link[1] : Arrays.copyOf(current, current.length + 1);
        current[current.length - 1] = link;
        inboundLinks.put(link.getTargetType(), current);
    }

    private static class AlreadyInitializedBuilder implements DirectLinkBuilder {
        private Link link;
        private OnPublish publish;

        public AlreadyInitializedBuilder(Link link, OnPublish publish) {
            this.link = link;
            this.publish = publish;
        }

        public DirectLinkBuilder addAnnotation(Glob annotation) {
            if (link.findAnnotation(annotation.getKey()) == null) {
                throw new GlobsException(link + " already initialized but doesn't contains " + annotation.getKey());
            }
            return this;
        }

        public MutableAnnotations addAnnotations(Collection<Glob> glob) {
            for (Glob g : glob) {
                addAnnotation(g);
            }
            return this;
        }

        public DirectLinkBuilder add(Field id1, Field id2) {
            if (!link.apply(new FieldMappingFunction() {
                boolean found = false;

                public void process(Field sourceField, Field targetField) {
                    if (id1 == sourceField && targetField == id2) {
                        found = true;
                    }
                }
            }).found) {
                throw new GlobsException(link + " already initialized but doesn't contains " + id1 + "=>" + id2);
            }

            return this;
        }

        public DirectLink publish() {
            publish.publish(link);
            return (DirectLink) link;
        }

        public Stream<Glob> streamAnnotations() {
            return link.streamAnnotations();
        }

        public boolean hasAnnotation(Key key) {
            return link.hasAnnotation(key);
        }

        public Glob getAnnotation(Key key) {
            return link.getAnnotation(key);
        }

        public Glob findAnnotation(Key key) {
            return link.findAnnotation(key);
        }

    }
}
