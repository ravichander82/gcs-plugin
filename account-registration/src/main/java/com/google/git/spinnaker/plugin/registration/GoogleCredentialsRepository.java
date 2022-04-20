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

import com.netflix.spinnaker.clouddriver.google.security.GoogleNamedAccountCredentials;
import com.netflix.spinnaker.credentials.CredentialsLifecycleHandler;
import com.netflix.spinnaker.credentials.MapBackedCredentialsRepository;
import com.netflix.spinnaker.credentials.definition.AbstractCredentialsLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

public class GoogleCredentialsRepository extends MapBackedCredentialsRepository<GoogleNamedAccountCredentials> {
    private AbstractCredentialsLoader<? extends GoogleNamedAccountCredentials> loader;

    public GoogleCredentialsRepository(
            @Lazy CredentialsLifecycleHandler<GoogleNamedAccountCredentials> eventHandler,
            @Lazy @Qualifier("googleCredentialsLoader") AbstractCredentialsLoader<? extends GoogleNamedAccountCredentials> loader) {
        super("git", eventHandler);
        this.loader = loader;
    }

    @Override
    public GoogleNamedAccountCredentials getOne(String key) {
        GoogleNamedAccountCredentials credentials = super.getOne(key);
        if (credentials == null) {
            System.out.println("Could not find account, {}. Checking remote repository." + key);
            loader.load();
            return super.getOne(key);
        }
        return credentials;
    }
}
