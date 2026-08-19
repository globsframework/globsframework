package org.globsframework.core.model.caller;

/**
 * Drives a {@link ToGlobCaller} : what the input says has to be written next.
 * <p>
 * Called once per turn of the loop, it answers the key of the {@link ToGlobFunction} to call — the key
 * of the SortedMap the caller was built from, an unknown key going to the fallback — or the {@code endLoop}
 * value the caller was built with, which ends the pass.
 */
public interface KeySource {

    int nextKey();

}
