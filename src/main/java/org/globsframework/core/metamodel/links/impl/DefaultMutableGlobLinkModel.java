package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.MutableGlobLinkModel;
import org.globsframework.core.metamodel.links.Link;
import org.globsframework.core.utils.collections.MapOfMaps;

import java.util.*;

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

    public LinkBuilder getLinkBuilder(String modelName, String name) {
        return getDirectLinkBuilder(modelName, name);
    }


    interface OnPublish {
        void publish(Link link);
    }

    public DirectLinkBuilder getDirectLinkBuilder(String modelName, String name, boolean required) {
        return new DefaultDirectLinkBuilder(modelName, name, required,
                (link) -> {
                    appendInSource(link);
                    appendInTarget(link);
                });
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
}
