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

import com.netflix.spinnaker.kork.plugins.api.spring.PrivilegedSpringPlugin;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.List;

public class GitAccountRegistrationPlugin extends PrivilegedSpringPlugin {

    public GitAccountRegistrationPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void registerBeanDefinitions(BeanDefinitionRegistry registry) {
        BeanDefinition lazyLoadCredentialsRepositoryDefinition = primaryBeanDefinitionFor(GoogleCredentialsRepository.class);
        try {
            System.out.println("Registering bean: {}"+ lazyLoadCredentialsRepositoryDefinition.getBeanClassName());
            registry.registerBeanDefinition("googleCredentialsRepository", lazyLoadCredentialsRepositoryDefinition);
        } catch (BeanDefinitionStoreException e) {
            System.out.println("Could not register bean {}"+ lazyLoadCredentialsRepositoryDefinition.getBeanClassName());
        }
        List<Class> classes = List.of(GoogleCredentialsDefinitionSource.class, GitAccountsStatus.class);
        for (Class classToAdd : classes) {
            BeanDefinition beanDefinition = beanDefinitionFor(classToAdd);
            try {
                System.out.println("Registering bean: {}"+ beanDefinition.getBeanClassName());
                registerBean(beanDefinition, registry);
            } catch (ClassNotFoundException e) {
                System.out.println("Could not register bean {}" + beanDefinition.getBeanClassName());
            }
        }
    }

    @Override
    public void start() {
        System.out.println("{} plugin started" + this.getClass().getSimpleName());
    }

    @Override
    public void stop() {
        System.out.println("{} plugin stopped" + this.getClass().getSimpleName());
    }
}