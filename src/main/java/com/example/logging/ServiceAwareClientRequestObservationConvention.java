package com.example.logging;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import io.micrometer.common.docs.KeyName;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;

public class ServiceAwareClientRequestObservationConvention extends DefaultClientRequestObservationConvention {

    private final String serviceName;

    public ServiceAwareClientRequestObservationConvention(String serviceName) {
        this.serviceName = serviceName;
    }

    public KeyValues getLowCardinalityKeyValues(ClientRequestObservationContext context) {
        return super.getLowCardinalityKeyValues(context).and(service(context));
    }

    protected KeyValue service(ClientRequestObservationContext context) {
        context.get("service");
//        return context.getCarrier() != null ? KeyValue.of(LowCardinalityKeyNames.METHOD, ((ClientHttpRequest) context.getCarrier()).getMethod().name()) : METHOD_NONE;
        return LowCardinalityKeyNames.SERVICE.withValue(serviceName);
    }

    public enum LowCardinalityKeyNames implements KeyName {
        SERVICE {
            public String asString() {
                return "service";
            }
        };
    }

}
