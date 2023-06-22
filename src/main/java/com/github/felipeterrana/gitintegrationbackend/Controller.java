package com.github.felipeterrana.gitintegrationbackend;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @PostMapping("/commit")
    public Map<String, Object> commit(Map<String, Object> request) {
        // TODO
        return new HashMap<String, Object>() {
        {
            put("sucess", true);
        }
        };
    }
}
