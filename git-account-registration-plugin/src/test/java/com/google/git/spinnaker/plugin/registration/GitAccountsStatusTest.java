package com.google.git.spinnaker.plugin.registration;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class GitAccountsStatusTest {

    @Test
    public void testFetchAccounts() {
        String accountsYml = "google:\n" +
                "  enabled: true\n" +
                "  accounts:\n" +
                "  - name: spinnaker-gce-account-v1.2\n" +
                "    jsonPath: /home/ashish/.gcp/gce-account.json\n" +
                "    consul:\n" +
                "      enabled: false\n" +
                "  primaryAccount: spinnaker-gce-account-v1.2\n" +
                "  bakeryDefaults:\n" +
                "    useInternalIp: false";
        String gitHttpsUsername = "opsmx";
        String gitHttpsPassword = "S3cret";
        String githubOAuthAccessToken = "ghp_5b58J65pqQhfDbKdsdrgDT9iq7pDPI3uciFh";
        String sshPrivateKeyFilePath = "/home/opsmx/.ssh/id_ed25519";
        String sshPrivateKeyPassphrase = "";
        String sshKnownHostsFilePath = "/home/opsmx/.ssh/known_hosts";
        Boolean sshTrustUnknownHosts = true;
        GitAccountsStatus gitAccountsStatus = spy(new GitAccountsStatus(gitHttpsUsername, gitHttpsPassword,
                githubOAuthAccessToken, sshPrivateKeyFilePath, sshPrivateKeyPassphrase, sshKnownHostsFilePath,
                sshTrustUnknownHosts));
        ReflectionTestUtils.setField(gitAccountsStatus, "repositoryName", "https://test.git");
        ReflectionTestUtils.setField(gitAccountsStatus, "filename", "accounts.yml");
        ReflectionTestUtils.setField(gitAccountsStatus, "credentialType", GitAccountsStatus.GitCredentialType.NONE);
        doReturn(new ByteArrayInputStream(accountsYml.getBytes())).when(gitAccountsStatus).downloadRemoteFile();
        assertTrue(gitAccountsStatus.fetchAccounts());
    }
}