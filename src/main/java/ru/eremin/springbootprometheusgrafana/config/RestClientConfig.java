package ru.eremin.springbootprometheusgrafana.config;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.JacksonXmlHttpMessageConverter;

import java.nio.charset.Charset;
import java.util.List;


@Configuration
public class RestClientConfig {

    @Bean
    public RestClientCustomizer windows1251RestClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .configureMessageConverters(converters -> {
                    converters.registerDefaults();

                    JacksonXmlHttpMessageConverter xmlConverter = new JacksonXmlHttpMessageConverter();

                    xmlConverter.setSupportedMediaTypes(List.of(
                            new MediaType("application", "xml", Charset.forName("windows-1251")),
                            new MediaType("text", "xml", Charset.forName("windows-1251"))
                    ));

                    converters.withXmlConverter(xmlConverter);
                });
    }
}
