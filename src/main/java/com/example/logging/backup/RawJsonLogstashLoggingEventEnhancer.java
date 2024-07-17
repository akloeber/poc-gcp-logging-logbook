package com.example.logging.backup;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.cloud.spring.logging.JsonLoggingEventEnhancer;
import net.logstash.logback.marker.RawJsonAppendingMarker;
import org.slf4j.Marker;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RawJsonLogstashLoggingEventEnhancer implements JsonLoggingEventEnhancer {

    public void enhanceJsonLogEntry(Map<String, Object> jsonMap, ILoggingEvent event) {
        //jsonMap.put("scott", Map.of("scotty", "tiger"));

        this.addLogstashMarkerIfNecessary(this.getFirstMarker(event.getMarkerList()), (marker) -> {
            jsonMap.put(marker.getFieldName(), marker.getFieldValue());
        });
    }

    private Marker getFirstMarker(List<Marker> markers) {
        return markers != null && !markers.isEmpty() ? markers.get(0) : null;
    }

    private void addLogstashMarkerIfNecessary(Marker marker, Consumer<RawJsonAppendingMarker> markerAdderFunction) {
        if (marker != null) {
            if (marker instanceof RawJsonAppendingMarker rawJsonAppendingMarker) {
                markerAdderFunction.accept(rawJsonAppendingMarker);
            }
        }
    }

}
