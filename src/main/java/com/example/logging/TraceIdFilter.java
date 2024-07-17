package com.example.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader("x-request-id");

        if (traceId != null) {
            MDC.put("traceId", traceId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            if (traceId != null) {
                MDC.remove("traceId");
            }
        }
    }
}
