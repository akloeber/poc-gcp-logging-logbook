package com.example.logging.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class LoggingController {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingController.class);
    private final LoggingService loggingService;

    public LoggingController(LoggingService loggingService) {

        this.loggingService = loggingService;
    }

    @GetMapping("/whoop")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void whoop() throws ExecutionException, InterruptedException {
        try {
            MDC.put("userId", "max.mustermann@example.com");
            // invoke via `curl -H "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01" http://localhost:8080/whoop -v`
            // invoke via `curl -H "X-Request-Id: 4bf92f3577b34da6a3ce929d0e0e4736" http://localhost:8080/whoop -v`
//            LOG.info("Request to /whoop");
            loggingService.whoop().get();
//            LOG.error("test error");
        } finally {
            MDC.remove("userId");
        }
    }
}
