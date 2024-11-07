package com.verve.verve_assignment.scheduler;

import com.verve.verve_assignment.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequestScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RequestScheduler.class);
    private final RequestService requestService;

    public RequestScheduler(RequestService requestService) {
        this.requestService = requestService;
    }

    @Scheduled(fixedRate = 60000)
    public void logUniqueRequestCount() {
        int count = requestService.getUniqueRequestCount();
        logger.info("Unique request count in the last minute: {}", count);
        
        // Clear the set after logging
        requestService.clearUniqueRequests();
    }

}