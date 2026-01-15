package org.globsframework.core.metamodel.links.impl;

import org.globsframework.core.metamodel.MutableGlobLinkModel;
import org.globsframework.core.metamodel.links.DirectLink;

class DefaultDirectLinkBuilder extends DefaultLinkBuilder<MutableGlobLinkModel.DirectLinkBuilder> implements MutableGlobLinkModel.DirectLinkBuilder {
    private final DefaultMutableGlobLinkModel.OnPublish publish;

    public DefaultDirectLinkBuilder(String modelName, String name, boolean required, DefaultMutableGlobLinkModel.OnPublish publish) {
        super(modelName, name, required);
        this.publish = publish;
    }

    public DirectLink publish() {
        DirectLink link = asDirectLink();
        publish.publish(link);
        return link;
    }

    DefaultDirectLinkBuilder getT() {
        return this;
    }
}
