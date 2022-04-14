package com.google.git.spinnaker.plugin.registration;

import com.google.common.collect.ImmutableList;
import com.google.git.GitAccountsStatus;
import com.netflix.spinnaker.clouddriver.google.config.GoogleConfigurationProperties;
import com.netflix.spinnaker.credentials.definition.CredentialsDefinitionSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GoogleCredentialsDefinitionSource implements CredentialsDefinitionSource<GoogleConfigurationProperties.ManagedAccount> {
    private final GitAccountsStatus accountsStatus;
    private final GoogleConfigurationProperties credentialsConfig;
    private List<GoogleConfigurationProperties.ManagedAccount> googleCredentialsDefinitions;

    @Autowired
    public GoogleCredentialsDefinitionSource(GitAccountsStatus accountsStatus, GoogleConfigurationProperties credentialsConfig) {
        this.accountsStatus = accountsStatus;
        this.credentialsConfig = credentialsConfig;
    }

    @Override
    public List<GoogleConfigurationProperties.ManagedAccount> getCredentialsDefinitions() {
        if (googleCredentialsDefinitions == null) {
            googleCredentialsDefinitions = credentialsConfig.getAccounts();
        }
        googleCredentialsDefinitions = accountsStatus.getGoogleAccountsAsList();
        return ImmutableList.copyOf(googleCredentialsDefinitions);
    }
}
