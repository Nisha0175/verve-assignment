package com.verve.verve_assignment.controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestPostController {

    @PostMapping("/test/post")
    public String handlePostRequest(@RequestBody String body) {
        // Echo back the received data
        return "Received data: " + body;
    }
}

