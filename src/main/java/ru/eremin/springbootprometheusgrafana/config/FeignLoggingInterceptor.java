package ru.eremin.springbootprometheusgrafana.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class FeignLoggingInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            log.info("=== FEIGN REQUEST ===");
            log.info("Method: {}", template.method());
            log.info("URL: {}{}", template.feignTarget().url(), template.path());
            log.info("Query params: {}", template.queries());
            log.info("Headers: {}", template.headers());

            if (template.body() != null) {
                log.info("Body: {}", new String(template.body()));
            }
            log.info("====================");
        };
    }
}
