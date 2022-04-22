package com.google.git.spinnaker.plugin.registration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class GitAccountsStatusTest {

    @Test
    public void fetchAccounts() {
        GitAccountsStatus gitAccountsStatus = mock(GitAccountsStatus.class);
        assertFalse(gitAccountsStatus.fetchAccounts());
    }
}