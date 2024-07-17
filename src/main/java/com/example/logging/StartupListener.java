package com.example.logging;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            MDC.put("userId", "system");
            MDC.put("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");
            LOG.info("Startup completed");

            LogstashMarker markers = Markers.append("confidential", "true")
                    .and(Markers.append("scott", "tiger"));
            LOG.info(markers, "A sensitive output");
            LOG.warn("Some test error", new RuntimeException());
        } finally {
            MDC.remove("traceId");
            MDC.remove("userId");
        }

//        LogstashMarker logstashMarker = Markers.append("logstashClasses", "value");
//        logstashMarker.add(Markers.append("foo", "bar"));
//        LogstashMarker logstashMarker = Markers.appendArray("logstashClasses", "LoggingService", "LoggingServiceImpl");
        LogstashMarker logstashMarker = Markers.appendRaw("logstashClasses", "{\"content\": \"LoggingService\"}");
        LOG.error(logstashMarker, "logstash markers");
    }
}
