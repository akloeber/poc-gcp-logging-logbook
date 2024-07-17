package com.example.logging.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class LoggingServiceImpl implements LoggingService {

    private final RestTemplate restTemplate;

    public LoggingServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final Logger LOG = LoggerFactory.getLogger(LoggingServiceImpl.class);

    @Override
    @Async
    public CompletableFuture<Void> whoop() {
////        Marker serviceMarker = MarkerFactory.getMarker("classes");
////        serviceMarker.add(MarkerFactory.getMarker("LoggingService"));
////        serviceMarker.add(MarkerFactory.getMarker("LoggingServiceImpl"));
//
//        LOG.atInfo().setMessage("whoop!").addMarker(serviceMarker).log();
//        LOG.info(serviceMarker, "whoop 1!");
//
//        LogstashMarker logstashMarker = Markers.append("logstashClasses", "tbd");
////        LogstashMarker logstashMarker = Markers.appendArray("logstashClasses", "LoggingService", "LoggingServiceImpl");
////        LogstashMarker logstashMarker = Markers.appendRaw("logstashClasses", "{\"content\": \"LoggingService\"}");
//        LOG.info(logstashMarker, "whoop 2!");

        // see https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-uri-building.html
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:3000/param?id={id}")
                .build(UUID.randomUUID());

//        String response = restTemplate.getForObject("http://localhost:3000/param?id={id}", String.class, UUID.randomUUID());
        String response = restTemplate.getForObject(
                "http://localhost:3000/param?id={id}",
                String.class,
                Map.of("id", UUID.randomUUID())
        );
        LOG.info("Response from upstream call: {}", response);
        return CompletableFuture.completedFuture(null);
    }
}
