package com.verve.verve_assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.verve.verve_assignment.service.RequestService;

@RestController
@RequestMapping("/api/verve")
public class VerveController {

    private final RequestService requestService;

    @Autowired
    public VerveController(RequestService requestService) {
        this.requestService = requestService;
    }

    // Endpoint for handling both GET and POST requests at /api/verve/accept
    @PostMapping("/accept")
    public String handleRequest(@RequestParam int id, 
                                @RequestParam(required = false) String endpoint, 
                                @RequestParam(defaultValue = "GET") String method) {
        return requestService.processRequest(id, endpoint, method);
    }

    // Endpoint to get the current unique request count
    @GetMapping("/accept/count")
    public int getUniqueRequestCount() {
        return requestService.getUniqueRequestCount();
    }

    // Endpoint to clear unique requests (e.g., reset for the next minute)
    @DeleteMapping("/accept/clear")
    public void clearUniqueRequests() {
        requestService.clearUniqueRequests();
    }
}
