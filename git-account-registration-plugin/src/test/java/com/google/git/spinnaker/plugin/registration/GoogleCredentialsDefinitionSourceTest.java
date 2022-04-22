//package com.google.git.spinnaker.plugin.registration;
//
//import com.netflix.spinnaker.clouddriver.google.config.GoogleConfigurationProperties;
//import static org.junit.Assert.assertFalse;
//import org.junit.Before;
//import org.junit.jupiter.api.Test;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.List;
//
//class GoogleCredentialsDefinitionSourceTest {
//
//    private GoogleCredentialsDefinitionSource googleCredentialsDefinitionSource;
//    private GitAccountsStatus accountsStatus;
//    private List<GoogleConfigurationProperties.ManagedAccount> googleCredentialsDefinitions;
//
//    @Before
//    public void setUp(){
//        googleCredentialsDefinitionSource = new GoogleCredentialsDefinitionSource();
//        accountsStatus = mock(GitAccountsStatus.class);
//        when(accountsStatus.fetchAccounts()).thenReturn(true);
//        when(accountsStatus.getGoogleAccountsAsList()).
//                thenReturn(List.of(mock(GoogleConfigurationProperties.ManagedAccount.class)));
//    }
//
//    @Test
//    void getCredentialsDefinitions() {
//        assertFalse(googleCredentialsDefinitionSource.getCredentialsDefinitions().isEmpty());
//    }
//}