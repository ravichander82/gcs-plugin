package com.google.git.spinnaker.plugin.registration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class GitAccountsStatusTest {

    private GitAccountsStatus gitAccountsStatus;

    @Test
    public void fetchAccounts() {
        gitAccountsStatus = mock(GitAccountsStatus.class);
        assertFalse(gitAccountsStatus.fetchAccounts());
    }
}