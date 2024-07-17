package com.example.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

@SpringBootApplication
@EnableAsync
public class LoggingPocApplication {

//    @Bean
//    public ClientRequestObservationConvention clientRequestObservationConvention() {
//        return new ServiceAwareClientRequestObservationConvention();
//    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, Logbook logbook) {
        LogbookClientHttpRequestInterceptor interceptor = new LogbookClientHttpRequestInterceptor(logbook);
        RestTemplate restTemplate = builder
                .additionalInterceptors(interceptor)
                .build();
        restTemplate.setObservationConvention(new ServiceAwareClientRequestObservationConvention("TestService.v1"));

        return restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(LoggingPocApplication.class, args);
    }

}
