package org.globsframework.core.model;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.utils.Strings;
import org.globsframework.core.utils.TestUtils;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DummyChangeSetListener implements ChangeSetListener {
    private ChangeSet lastChanges;
    private Set<GlobType> lastResetTypes;

    public void globsChanged(ChangeSet changeSet, GlobRepository globRepository) {
        lastChanges = changeSet;
    }

    public void globsReset(GlobRepository globRepository, Set<GlobType> changedTypes) {
        lastResetTypes = changedTypes;
    }

    public void assertLastChangesEqual(String expectedXml) {
        if (Strings.isNullOrEmpty(expectedXml)) {
            assertNoChanges();
            return;
        }
        if (lastChanges == null) {
            fail("No changes received");
        }
        GlobTestUtils.assertChangesEqual(lastChanges, expectedXml);
    }

    public void assertLastChangesEqual(GlobType type, String expectedXml) {
        if (lastChanges == null) {
            fail("No changes received");
        }
        GlobTestUtils.assertChangesEqual(lastChanges, type, expectedXml);
    }

    public void assertLastChangesEqual(List<Key> keys, String expectedXml) {
        if (lastChanges == null) {
            fail("No changes received");
        }
        GlobTestUtils.assertChangesEqual(lastChanges, keys, expectedXml);
    }

    public void assertNoChanges(GlobType type) {
        if ((lastChanges != null) && lastChanges.containsChanges(type)) {
            fail("Unexpected changes: " + lastChanges);
        }
    }

    public void assertNoChanges() {
        if ((lastChanges != null) && !lastChanges.isEmpty()) {
            fail("Unexpected changes: " + lastChanges);
        }
    }

    public ChangeSet getLastChanges() {
        return lastChanges;
    }

    public void reset() {
        lastChanges = null;
    }

    public void assertResetListEquals(GlobType... types) {
        this.lastChanges = null;
        if (lastResetTypes == null) {
            fail("reset was not called");
        }
        TestUtils.assertSetEquals(lastResetTypes, Arrays.asList(types));
    }
}
