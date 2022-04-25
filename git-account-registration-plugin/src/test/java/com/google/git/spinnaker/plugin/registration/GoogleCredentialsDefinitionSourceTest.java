package com.google.git.spinnaker.plugin.registration;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

class GoogleCredentialsDefinitionSourceTest {

    @Test
    void testGetCredentialsDefinitions() {
        GoogleCredentialsDefinitionSource googleCredentialsDefinitionSource = new GoogleCredentialsDefinitionSource();
        GitAccountsStatus accountsStatus = mock(GitAccountsStatus.class);
        ReflectionTestUtils.setField(googleCredentialsDefinitionSource, "accountsStatus", accountsStatus);
        when(accountsStatus.fetchAccounts()).thenReturn(true);
        googleCredentialsDefinitionSource.getCredentialsDefinitions();
        verify(accountsStatus, times(1)).fetchAccounts();
    }
}
