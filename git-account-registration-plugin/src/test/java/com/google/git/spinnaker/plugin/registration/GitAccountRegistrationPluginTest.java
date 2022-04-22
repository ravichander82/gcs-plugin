package com.google.git.spinnaker.plugin.registration;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GitAccountRegistrationPluginTest {

    private GitAccountRegistrationPlugin gitAccountRegistrationPlugin;

    @Test
    public void registerBeanDefinitions() {
        gitAccountRegistrationPlugin = new GitAccountRegistrationPlugin(mock(PluginWrapper.class));
        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
        doNothing().when(registry).registerBeanDefinition(any(), any());
        gitAccountRegistrationPlugin.registerBeanDefinitions(registry);
        verify(registry, times(3)).registerBeanDefinition(any(), any());
    }
}