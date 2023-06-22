package com.github.felipeterrana.gitintegrationbackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    public static final String REPOSITORY_REF = "repository/";

    @PostMapping("/commit")
    @ResponseBody
    public Map<String, Object> commit(@RequestBody Map<String, String> request) {
        try {
            String path = "repository/" + request.get("path");
            byte[] fileContent = Base64.getDecoder().decode(request.get("contentBase64"));

            createFile(path, fileContent);

            // Initialize the JGit repository
            Repository repository = new FileRepository("repository/.git");
            Git git = new Git(repository);

            // Add the file to the repository
            git.add().addFilepattern(request.get("path")).call();

            // Create a CommitCommand
            CommitCommand commitCommand = git.commit();

            // Set the author email
            String authorName = request.get("authorName");
            String authorEmail = request.get("authorEmail");
            
            PersonIdent author = new PersonIdent(authorName, authorEmail);            

            // Set the author for the commit
            commitCommand.setAuthor(author);

            // Commit the changes
            commitCommand.setMessage("Commit").call();

            // Close the repository
            repository.close();

            return createSuccessResponse();
        } catch (Exception e) {
            return createErrorResponse("An error occurred during the commit operation: " + e.getLocalizedMessage());
        }
    }

    private static void createFile(String path, byte[] content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content);
        }
    }

    private static Map<String, Object> createSuccessResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }
    
    private static Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
