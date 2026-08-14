package org.globsframework.core.model.generate.write;

/**
 * How a module that can generate bytecode offers the write callers of the whole process.
 * <p>
 * The same shape as {@link org.globsframework.core.model.generate.read.GenerateCallerService} and installed
 * the same way, with a class name on the command line :
 * {@code -Dglobs.callerWrite=<fully qualified class name>}. That keeps core free of any dependency on the
 * generator while letting {@link GeneratedFunctionCallerWrite#get()} answer a generated caller to a parser
 * that only knows this package.
 * <pre>
 * -Dglobs.callerWrite=org.globsframework.model.generator.AsmCallerWriteGeneratorService
 * </pre>
 * There is no GlobType here, unlike on the read side : a write caller is built from functions alone, so the
 * service is asked once and answers for everything. Unset — the default — means {@code get()} keeps answering
 * a {@link DefaultFunctionCallerWrite}. A name that cannot be loaded throws rather than falling back
 * silently : it was asked for explicitly.
 */
public interface GenerateCallerWriteService {

    /**
     * @return null when this service has nothing to offer after all — a generator that found its JVM unable
     * to define classes, say. It is not a refusal to be reported, and {@code get()} then falls back to the
     * loop.
     */
    GeneratedFunctionCallerWrite getGenerateCallerWrite();

    class Builder {
        // written at startup (or by a test through reset) and read from anywhere afterwards
        static private volatile GenerateCallerWriteService service = load();

        public static GenerateCallerWriteService getService() {
            return service;
        }

        /** Re-reads the property. Mandatory when a test changes it, the service being cached. */
        public static void reset() {
            service = load();
        }

        private static GenerateCallerWriteService load() {
            String className = System.getProperty("globs.callerWrite");
            if (className == null) {
                return null;
            }
            try {
                return (GenerateCallerWriteService) Class.forName(className).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("fail to load " + className, e);
            }
        }
    }
}
