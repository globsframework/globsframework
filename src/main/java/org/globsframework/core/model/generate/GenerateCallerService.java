package org.globsframework.core.model.generate;

import org.globsframework.core.metamodel.GlobType;

/**
 * How a module that can generate bytecode offers callers for types whose factory is <em>not</em> a
 * {@link GlobGenerateFactory} — core's own DefaultGlob, typically.
 * <p>
 * The same shape as {@link org.globsframework.core.model.GlobFactoryService} and installed the same way, with
 * a class name on the command line : {@code -Dglobs.caller=<fully qualified class name>}. That keeps core free
 * of any dependency on the generator while letting {@link GenerateCaller#callerFor} answer a generated caller
 * to a codec that only knows this package.
 * <pre>
 * -Dglobs.caller=org.globsframework.model.generator.AsmCallerGeneratorService
 * </pre>
 * Unset — the default — means {@code callerFor} keeps answering a {@link DefaultFunctionCaller}. A name that
 * cannot be loaded throws rather than falling back silently : it was asked for explicitly.
 */
public interface GenerateCallerService {

    /**
     * @return null when this service cannot build a caller for that type — it is not a refusal to be reported,
     * just "not mine", and the caller then falls back to the loop.
     */
    GenerateCaller getGenerateCaller(GlobType type);

    class Builder {
        // written at startup (or by a test through reset) and read from anywhere afterwards
        static private volatile GenerateCallerService service = load();

        public static GenerateCallerService getService() {
            return service;
        }

        /** Re-reads the property. Mandatory when a test changes it, the service being cached. */
        public static void reset() {
            service = load();
        }

        private static GenerateCallerService load() {
            String className = System.getProperty("globs.caller");
            if (className == null) {
                return null;
            }
            try {
                return (GenerateCallerService) Class.forName(className).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("fail to load " + className, e);
            }
        }
    }
}
