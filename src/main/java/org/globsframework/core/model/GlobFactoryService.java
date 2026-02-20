package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.impl.DefaultGlobFactoryService;

public interface GlobFactoryService {

    GlobFactory getFactory(GlobType type);

    class Builder {
        static private GlobFactoryService builderFactory =
                new DispatchGlobFactoryService();

        public static GlobFactoryService getBuilderFactory() {
            return builderFactory;
        }

        public static void reset() {
            builderFactory = new DispatchGlobFactoryService();
        }
    }

    class DispatchGlobFactoryService implements GlobFactoryService {
        final GlobFactoryService globFactoryService = new DefaultGlobFactoryService();
        final GlobFactoryService specialized;

        public DispatchGlobFactoryService() {
            String className = System.getProperty("org.globsframework.builder");
            if (className == null) {
                specialized = globFactoryService;
            }
            else {
                try {
                    specialized = (GlobFactoryService) Class.forName(className).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("fail to load" + className, e);
                }
            }
        }

        public GlobFactory getFactory(GlobType type) {
            GlobFactory globFactory = specialized.getFactory(type);
            return globFactory != null ? globFactory : globFactoryService.getFactory(type);
        }
    }
}
