package com.github.felipeterrana.gitintegrationbackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @PostMapping("/commit")
    @ResponseBody
    public Map<String, Object> commit(@RequestBody Map<String, String> request) throws IOException {
        String path = "repository/" + request.get("path");
        byte[] fileContent = Base64.getDecoder().decode(request.get("contentBase64"));

        createFile(path, fileContent);

        return new HashMap<String, Object>() {
        {
            put("sucess", true);
        }
        };
    }

    private static void createFile(String path, byte[] content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(content);

        outputStream.close();
    }
}
