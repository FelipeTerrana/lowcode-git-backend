package com.github.felipeterrana.gitintegrationbackend;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartRepositoryRunner implements CommandLineRunner {

  public static final String REPOSITORY_DIR = "repository";
  public static final String REPOSITORY_URL = "https://github.com/ufjf-joaopauloaraujo/budibase-repository-test.git";

  @Override
  public void run(String... args) throws Exception {
    File repositoryDir = new File(REPOSITORY_DIR);

    if (repositoryDir.exists()) {
      Git.open(repositoryDir)
          .pull()
          .call();
    } else {
      repositoryDir.mkdir();

      Git.cloneRepository()
          .setURI(REPOSITORY_URL)
          .setDirectory(repositoryDir)
          .setBare(false)
          .setCredentialsProvider( new UsernamePasswordCredentialsProvider( "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", "" ) )
          .call();
    }
  }
}