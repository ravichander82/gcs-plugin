//package com.google.git.spinnaker.plugin.registration;
//
//import com.netflix.spinnaker.kork.plugins.api.spring.PrivilegedSpringPlugin;
//import org.junit.Before;
//import org.junit.jupiter.api.Test;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import org.pf4j.PluginWrapper;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//
//public class GitAccountRegistrationPluginTest {
//
//    private GitAccountRegistrationPlugin gitAccountRegistrationPlugin;
//
//    @Before
//    void setUp() {
//        this.gitAccountRegistrationPlugin = new GitAccountRegistrationPlugin(mock(PluginWrapper.class));
//    }
//
//    @Test
//    void registerBeanDefinitions() {
//        BeanDefinitionRegistry registry = mock(BeanDefinitionRegistry.class);
//        doNothing().when(mock(PrivilegedSpringPlugin.class)).registerBeanDefinitions(any());
//        gitAccountRegistrationPlugin.registerBeanDefinitions(any());
//        verify(gitAccountRegistrationPlugin, times(1)).registerBeanDefinitions(registry);
//    }
//
//    @Test
//    void start() {
//    }
//
//    @Test
//    void stop() {
//    }
//}