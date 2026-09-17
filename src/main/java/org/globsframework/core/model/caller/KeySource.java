package org.globsframework.core.model.caller;

/**
 * Drives the dispatching caller of
 * {@link ToGlobCallerFactory#create(String, java.util.SortedMap, Object, int, Class, Class, Class[])} : what
 * the input says has to be written next.
 * <p>
 * Called once per turn of the loop, it answers the key of the function to call — the key of the SortedMap the
 * caller was built from, an unknown key going to the fallback — or the {@code endLoop} value the caller was
 * built with, which ends the pass.
 * <p>
 * It is one of the caller's own arguments rather than a parameter of its own : a parser's input is normally
 * both the thing that says what comes next and the thing the functions read from, so it is passed once.
 */
public interface KeySource {

    int nextKey();

}
