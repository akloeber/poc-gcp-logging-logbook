package com.example.logging;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.*;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.io.IOException;
import java.util.Map;

public class LogstashMarkerEnhancingSink implements Sink {

    private static final int REQUEST_FORMAT_BUFFER = 2048;
    private static final int RESPONSE_FORMAT_BUFFER = 2048;

    private final Logger log = LoggerFactory.getLogger(Logbook.class);

    private final JsonHttpLogFormatter formatter = new JsonHttpLogFormatter();

    @Override
    public boolean isActive() {
        return log.isTraceEnabled();
    }

    @Override
    public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
        LogstashMarker logstashMarker = Markers.empty();

        Map<String, Object> attributes = formatter.prepare(precorrelation, request);
        attributes.forEach((k, v) -> logstashMarker.add(new ObjectAppendingMarker(k, v)));

        log.trace(logstashMarker, formatRequest(request));
    }

    @Override
    public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
        LogstashMarker logstashMarker = Markers.empty();

        Map<String, Object> attributes = formatter.prepare(correlation, response);
        attributes.forEach((k, v) -> logstashMarker.add(new ObjectAppendingMarker(k, v)));

        log.trace(logstashMarker, formatResponse(correlation, request, response));
    }

    private static String formatResponse(Correlation correlation, HttpRequest request, HttpResponse response) {
        // -> "Outgoing Response: GET http://localhost:8080/whoop - 204 (No Content) HTTP/1.1 32ms"
        final StringBuilder result = new StringBuilder(RESPONSE_FORMAT_BUFFER);

        result.append(direction(response));
        result.append(" Response: ");
        result.append(request.getMethod());
        result.append(' ');
        RequestURI.reconstruct(request, result);

        result.append(" - ");
        result.append(response.getStatus());
        result.append(' ');
        final String reasonPhrase = response.getReasonPhrase();
        if (reasonPhrase != null) {
            result.append("(");
            result.append(reasonPhrase);
            result.append(") ");
        }
        result.append(response.getProtocolVersion());
        result.append(" ");
        result.append(correlation.getDuration().toMillis());
        result.append("ms");

        return result.toString();
    }

    private static String formatRequest(HttpRequest request) {
        // -> "Incoming Request: GET http://localhost:8080/scott/tiger HTTP/1.1"
        final StringBuilder result = new StringBuilder(REQUEST_FORMAT_BUFFER);

        result.append(direction(request));
        result.append(" Request: ");
        result.append(request.getMethod());
        result.append(' ');
        RequestURI.reconstruct(request, result);
        result.append(' ');
        result.append(request.getProtocolVersion());

        return result.toString();
    }

    private static String direction(final HttpMessage request) {
        return request.getOrigin() == Origin.REMOTE ? "Incoming" : "Outgoing";
    }
}
