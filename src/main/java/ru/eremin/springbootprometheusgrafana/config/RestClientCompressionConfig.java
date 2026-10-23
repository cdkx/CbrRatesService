package ru.eremin.springbootprometheusgrafana.config;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;


@Configuration
public class RestClientCompressionConfig {

    @Bean
    public RestClientCustomizer disableCompressionCustomizer() {
        return builder -> {
            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();

            requestFactory.enableCompression(false);

            builder.requestFactory(requestFactory);
        };
    }
}
