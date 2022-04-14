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

package com.google.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@PluginConfiguration
public class GitSourceConfig {
  private UsernamePasswordCredentialsProvider httpsUsernamePasswordCredentialsProvider;
  private UsernamePasswordCredentialsProvider httpsOAuthCredentialsProvider;
  private TransportConfigCallback sshTransportConfigCallback;

  @Value("${gitSourceConfig.repositoryName:#{null}}")
  private String repositoryName;
  @Value("${gitSourceConfig.filename:#{null}}")
  private String filename;
  @Value("#{'${gitSourceConfig.credentialType}'.toUpperCase()}")
  private GitCredentialType credentialType;

  @Autowired
  public GitSourceConfig(@Value("${gitSourceConfig.gitHttpsUsername:#{null}}") String gitHttpsUsername,
                         @Value("${gitSourceConfig.gitHttpsPassword:#{null}}") String gitHttpsPassword,
                         @Value("${gitSourceConfig.githubOAuthAccessToken:#{null}}") String githubOAuthAccessToken,
                         @Value("${gitSourceConfig.sshPrivateKeyFilePath:#{null}}") String sshPrivateKeyFilePath,
                         @Value("${gitSourceConfig.sshPrivateKeyPassphrase:#{null}}") String sshPrivateKeyPassphrase,
                         @Value("${gitSourceConfig.sshKnownHostsFilePath:#{null}}") String sshKnownHostsFilePath,
                         @Value("${gitSourceConfig.sshTrustUnknownHosts:false}") Boolean sshTrustUnknownHosts) {
    setHttpsUsernamePasswordCredentialsProvider(gitHttpsUsername, gitHttpsPassword);
    setHttpsOAuthCredentialsProvider(githubOAuthAccessToken);
    setSshPrivateKeyTransportConfigCallback(sshPrivateKeyFilePath, sshPrivateKeyPassphrase, sshKnownHostsFilePath,
            sshTrustUnknownHosts);
  }

  public GitAccountsStatus buildRepositoryClient(GitSourceConfig gitSourceConfig) {
    return new GitAccountsStatus(gitSourceConfig);
  }

  public List<GitCredentialType> getSupportedCredentialTypes() {
    List<GitCredentialType> supportedTypes = List.of(GitCredentialType.NONE);

    if (httpsUsernamePasswordCredentialsProvider != null) {
      supportedTypes.add(GitCredentialType.HTTPS_USERNAME_PASSWORD);
    }

    if (httpsOAuthCredentialsProvider != null) {
      supportedTypes.add(GitCredentialType.HTTPS_GITHUB_OAUTH_TOKEN);
    }

    if (sshTransportConfigCallback != null) {
      supportedTypes.add(GitCredentialType.SSH);
    }

    return supportedTypes;
  }

  public void setHttpsUsernamePasswordCredentialsProvider(String gitHttpsUsername, String gitHttpsPassword) {
    if (!StringUtils.isEmpty(gitHttpsUsername) && !StringUtils.isEmpty(gitHttpsPassword)) {
      httpsUsernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitHttpsUsername,
              gitHttpsPassword);
    }
  }

  public void setHttpsOAuthCredentialsProvider(String githubOAuthAccessToken) {
    if (!StringUtils.isEmpty(githubOAuthAccessToken)) {
      httpsOAuthCredentialsProvider = new UsernamePasswordCredentialsProvider(githubOAuthAccessToken, "");
    }
  }

  public void setSshPrivateKeyTransportConfigCallback(String sshPrivateKeyFilePath,
                                               String sshPrivateKeyPassphrase,
                                               String sshKnownHostsFilePath,
                                               boolean sshTrustUnknownHosts) {
    if (!StringUtils.isEmpty(sshPrivateKeyFilePath) && !StringUtils.isEmpty(sshPrivateKeyPassphrase)) {
      SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
        @Override
        protected void configure(OpenSshConfig.Host hc, Session session) {
          if (sshKnownHostsFilePath == null && sshTrustUnknownHosts) {
            session.setConfig("StrictHostKeyChecking", "no");
          }
        }

        @Override
        protected JSch createDefaultJSch(FS fs) throws JSchException {
          JSch defaultJSch = super.createDefaultJSch(fs);
          defaultJSch.addIdentity(sshPrivateKeyFilePath, sshPrivateKeyPassphrase);

          if (sshKnownHostsFilePath != null && sshTrustUnknownHosts) {
            log.warn("SSH known_hosts file path supplied, ignoring 'sshTrustUnknownHosts' option");
          }
          if (sshKnownHostsFilePath != null) {
            defaultJSch.setKnownHosts(sshKnownHostsFilePath);
          }

          return defaultJSch;
        }
      };

      sshTransportConfigCallback = transport -> {
        SshTransport sshTransport = (SshTransport) transport;
        sshTransport.setSshSessionFactory(sshSessionFactory);
      };
    }
  }
}
