package com.github.felipeterrana.gitintegrationbackend;

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
    public Map<String, Object> commit(@RequestBody Map<String, String> request) {
        // TODO
        return new HashMap<String, Object>() {
        {
            put("sucess", true);
        }
        };
    }
}
