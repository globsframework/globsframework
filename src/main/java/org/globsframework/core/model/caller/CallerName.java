package org.globsframework.core.model.caller;

/**
 * The identity a codec gives to a caller it asks for, on both sides
 * ({@link FromGlobCallerFactory#callerFor} and {@link ToGlobCallerFactory#create}).
 * <p>
 * It exists for the generating implementations : they emit a class per call, and that class needs a name
 * that is the same from one run of the application to the next, so that the JVM can recognise it — an AOT
 * cache matches a class of a user-defined loader on its name and its bytes, and a name taken from a counter
 * matches nothing. What the caller supplies is the *purpose* of the caller ({@code "binser.write"},
 * {@code "grpc.read"}), which is the only half a generator cannot know; the other half — the type and the
 * shape it is generating over — it adds itself.
 * <p>
 * The rule is therefore : one purpose per code path that builds callers, constant in the source. A name
 * built from something that varies per run (an instance count, a hash code, a timestamp) is accepted here
 * and silently gives up the identity it was asked for.
 * <p>
 * Any non-blank String is a valid name : a generator sanitises it to make a class name out of it and
 * disambiguates what sanitising made equal, so a caller never has to know what a JVM identifier accepts. The
 * check is here rather than in the generators so that a name is refused the same way on a JVM where nothing
 * is installed to generate — the loops ignore the name entirely, and would otherwise take anything.
 */
public final class CallerName {

    private CallerName() {
    }

    /** @return name, so it can be checked in place. */
    public static String check(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A caller needs a name identifying what builds it, e.g. "
                                               + "\"binser.write\" : it is what the generated class is named "
                                               + "after, and what makes that name the same from one run to "
                                               + "the next.");
        }
        return name;
    }
}
