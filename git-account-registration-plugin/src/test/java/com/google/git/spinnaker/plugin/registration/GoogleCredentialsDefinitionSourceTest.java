package com.google.git.spinnaker.plugin.registration;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class GoogleCredentialsDefinitionSourceTest {

    @InjectMocks
    private GoogleCredentialsDefinitionSource googleCredentialsDefinitionSource;
    private GitAccountsStatus accountsStatus;

    @Test
    void getCredentialsDefinitions() {
        googleCredentialsDefinitionSource = new GoogleCredentialsDefinitionSource();
        accountsStatus = mock(GitAccountsStatus.class);
        ReflectionTestUtils.setField(googleCredentialsDefinitionSource, "accountsStatus", accountsStatus);
        when(accountsStatus.fetchAccounts()).thenReturn(true);
        googleCredentialsDefinitionSource.getCredentialsDefinitions();
        verify(accountsStatus, times(1)).fetchAccounts();
    }
}
