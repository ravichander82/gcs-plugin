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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.netflix.spinnaker.clouddriver.google.config.GoogleConfigurationProperties;
import com.netflix.spinnaker.kork.secrets.SecretException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@Slf4j
@NoArgsConstructor
public class GitAccountsStatus {
    private GitSourceConfig gitSourceConfig;
    private static final String IDENTIFIER = "git";
    private static final String APPLICATION_NAME = "Spinnaker";
    private String targetDirectory = System.getProperty("java.io.tmpdir");

    @Autowired
    public GitAccountsStatus(GitSourceConfig gitSourceConfig) {
        this.gitSourceConfig = gitSourceConfig;
    }

    public void initializeLocalDirectory() throws GitAPIException {
        CloneCommand command = Git.cloneRepository()
                .setURI(gitSourceConfig.getRepositoryName())
                .setDirectory(new File(targetDirectory));
        attachCredentials(command);
        Git git = command.call();
    }

    public void updateLocalDirectoryWithVersion(String version) throws GitAPIException, IOException {
        fetch();
        checkout(version);
    }

    public void fetch() throws IOException, GitAPIException {
        Repository repo = FileRepositoryBuilder.create(new File(targetDirectory, ".git"));
        FetchCommand command = new Git(repo).fetch();
        attachCredentials(command);
        FetchResult fetchResult = command.call();
    }

    public void checkout(String branch) throws IOException, GitAPIException {
        Repository repo = FileRepositoryBuilder.create(new File(targetDirectory, ".git"));
        Ref ref = new Git(repo).checkout().setName("origin/$branch").call();
    }

    private <T extends TransportCommand> void attachCredentials(T command) {
        switch (gitSourceConfig.getCredentialType()) {
            case HTTPS_USERNAME_PASSWORD:
                command.setCredentialsProvider(gitSourceConfig.getHttpsUsernamePasswordCredentialsProvider());
                break;
            case HTTPS_GITHUB_OAUTH_TOKEN:
                command.setCredentialsProvider(gitSourceConfig.getHttpsOAuthCredentialsProvider());
                break;
            case SSH:
                command.setTransportConfigCallback(gitSourceConfig.getSshTransportConfigCallback());
                break;
            case NONE:
            default:
                break;
        }
    }

    public InputStream downloadRemoteFile() {
        try {
            Repository repository = new FileRepository(gitSourceConfig.getRepositoryName());
            ObjectId lastCommitId = repository.resolve(Constants.HEAD);
            RevWalk revWalk = new RevWalk(repository);
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(gitSourceConfig.getFilename()));
            if (!treeWalk.next()) {
                throw new IllegalStateException("Unable to download file.");
            }
            ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
            log.info("File Size : " + loader.getSize() + "bytes");
            InputStream inputStream = new ByteArrayInputStream(loader.getBytes());
            revWalk.dispose();
            return inputStream;
        } catch (IOException e) {
            throw new SecretException(String.format("Error reading contents of Git. Bucket: %s, " +
                    "Object: %s.\nError: %s", gitSourceConfig.getRepositoryName(), gitSourceConfig.getFilename(), e.getMessage()));
        }
    }

    public boolean getDesiredAccounts() {
        return true;
    }

    public List<GoogleConfigurationProperties.ManagedAccount> getGoogleAccountsAsList() {
        List<GoogleConfigurationProperties.ManagedAccount> googleCredentialsDefinitions = new ArrayList<>();
        InputStream bucket = downloadRemoteFile();
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(bucket);
        HashMap map = (HashMap) data.get("google");
        Boolean isEnabled = (Boolean) map.get("enabled");
        ArrayList accountsList = (ArrayList) map.get("accounts");
        ObjectMapper mapper = new ObjectMapper();
        for( int i =0 ;i<accountsList.size();i++) {
            GoogleConfigurationProperties.ManagedAccount managedAccount = mapper.
                    convertValue(accountsList.get(i), GoogleConfigurationProperties.ManagedAccount.class);
            googleCredentialsDefinitions.add(managedAccount);
            log.info("Google account ====================  " , managedAccount.toString());
        }
        return ImmutableList.copyOf(googleCredentialsDefinitions);
    }
}
