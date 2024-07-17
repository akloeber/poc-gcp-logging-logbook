package com.example.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.*;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class LogbookConfiguration {

    private static class MyHttpLogWriter implements HttpLogWriter {

        private final Logger log = LoggerFactory.getLogger(Logbook.class);

        @Override
        public boolean isActive() {
            return log.isTraceEnabled();
        }

        @Override
        public void write(final Precorrelation precorrelation, final String request) {
            log.trace(request);
        }

        @Override
        public void write(final Correlation correlation, final String response) {
            log.trace(response);
        }

    }

    private static final class OnlyHeaders implements HeaderFilter {

        private final Set<String> includedHeaders;

        public OnlyHeaders(String... includedHeaders) {
            this.includedHeaders = Arrays.stream(includedHeaders).map(String::toLowerCase).collect(Collectors.toSet());
        }

        @Override
        public HttpHeaders filter(HttpHeaders headers) {
            return headers.delete((k, v) -> !includedHeaders.contains(k.toLowerCase()));
        }
    }

    @Bean
    public Logbook logbook() {
        HttpLogFormatter formatter = new JsonHttpLogFormatter();
//        HttpLogWriter httpLogWriter = new DefaultHttpLogWriter();
        HttpLogWriter httpLogWriter = new MyHttpLogWriter();
        DefaultSink sink = new DefaultSink(formatter, httpLogWriter);
        LogstashMarkerEnhancingSink logstashMarkerEnhancingSink = new LogstashMarkerEnhancingSink();

        // Incoming Request: 830b68444a3dc815\nRemote: 0:0:0:0:0:0:0:1\nGET http://localhost:8080/whoop HTTP/1.1\naccept: */*\nhost: localhost:8080\nuser-agent: curl/8.6.0
        // Outgoing Response: 830b68444a3dc815\nDuration: 20 ms\nHTTP/1.1 204 No Content\nDate: Thu, 11 Jul 2024 11:44:52 GMT

        // Incoming Request: GET http://localhost:8080/whoop HTTP/1.1
        // Outgoing Response: GET http://localhost:8080/whoop HTTP/1.1 - 204 (No Content) HTTP/1.1

        return Logbook.builder()
                .sink(logstashMarkerEnhancingSink)
                .headerFilter(new OnlyHeaders("content-type", "content-length"))

//                .condition(exclude(
//                        requestTo("/health"),
//                        requestTo("/admin/**"),
//                        contentType("application/octet-stream"),
//                        header("X-Secret", newHashSet("1", "true")::contains)))
                .build();
    }
}
