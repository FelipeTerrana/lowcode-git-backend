package com.github.felipeterrana.gitintegrationbackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/blame")
    @ResponseBody
    public Map<String, Object> blame(@RequestBody Map<String, String> request) {
        try (Repository repository = new FileRepository(StartRepositoryRunner.REPOSITORY_DIR + "/.git"); Git git = new Git(repository)) {
            BlameResult blameResult = git.blame()
                .setFilePath(request.get("path"))
                .call();

            Map<Integer, String> blameData = new HashMap<>();

            for (int i = 0; i < blameResult.getResultContents().size(); i++) {
                blameData.put(i, blameResult.getSourceCommit(i).toString()
                + " - " + blameResult.getSourceAuthor(i).getEmailAddress()
                + " - " + blameResult.getSourceAuthor(i).getWhen());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", blameData);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("An error occurred during blame operation: " + e.getLocalizedMessage());
        }
    }


    @PostMapping("/commit")
    @ResponseBody
    public Map<String, Object> commit(@RequestBody Map<String, String> request) {
        try (Repository repository = new FileRepository(StartRepositoryRunner.REPOSITORY_DIR + "/.git"); Git git = new Git(repository)) {
            System.out.println("request:"+ request);

            String path = "repository/" + request.get("path");
            byte[] fileContent = Base64.getDecoder().decode(request.get("contentBase64"));

            createFile(path, fileContent);

            File repositoryDir = new File(StartRepositoryRunner.REPOSITORY_DIR);

            git.open(repositoryDir)
                .add().addFilepattern(request.get("path"))
                .call();

            // Set the author email
            String authorEmail = request.get("authorEmail");// "example@example.com";

            git.commit()
                .setAuthor(authorEmail, authorEmail)
                .setMessage(request.get("path"))
                .call();


            // 403
            // git.push()
            //     .setCredentialsProvider( new UsernamePasswordCredentialsProvider( "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", "" ) )
            //     .call();

            System.out.println("Sucesso!");
            return createSuccessResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("An error occurred during the commit operation: " + e.getLocalizedMessage());
        }
    }

    private static void createFile(String path, byte[] content) throws IOException {
        File file = new File(path);
        file.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content);
            System.out.println("Arquivo alterado!");
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
