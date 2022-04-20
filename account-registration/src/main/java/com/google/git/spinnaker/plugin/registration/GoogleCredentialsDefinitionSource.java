/*
 * Copyright 2022 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.git.spinnaker.plugin.registration;

import com.google.common.collect.ImmutableList;
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
        System.out.println("getCredentialsDefinitions ====================  ");
        if (googleCredentialsDefinitions == null) {
            googleCredentialsDefinitions = credentialsConfig.getAccounts();
        }
        if (accountsStatus.fetchAccounts()) {
            googleCredentialsDefinitions = accountsStatus.getGoogleAccountsAsList();
        }
        return ImmutableList.copyOf(googleCredentialsDefinitions);
    }
}
