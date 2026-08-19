package org.globsframework.core.model.caller;

import org.globsframework.core.model.GlobFactory;

/**
 * The GlobFactory of a type whose implementation is generated, which can also generate callers for it.
 * <p>
 * Nothing in core implements this : it is what a module like globs-generate puts on its factories, and what
 * a codec tests for. Most callers should not test it by hand and should ask {@link FromGlobCallerFactory#callerFor},
 * which falls back to a {@link LoopFromGlobCaller} for a type that has no generated factory instead of
 * leaving them with a second code path.
 * <pre>
 * FromGlobCaller&lt;Out, Void&gt; caller =
 *     type.getGlobFactory() instanceof CallerGlobFactory generate
 *         ? generate.create("mycodec.write", field -&gt; functionFor(field))
 *         : null;   // not generated
 * </pre>
 */
public interface CallerGlobFactory extends GlobFactory, FromGlobCallerFactory {
}
