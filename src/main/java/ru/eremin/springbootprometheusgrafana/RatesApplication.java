package ru.eremin.springbootprometheusgrafana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.service.registry.ImportHttpServices;
import ru.eremin.springbootprometheusgrafana.apiclient.CbrClient;


@EnableScheduling
@ImportHttpServices(group = "cbr", types = CbrClient.class)
@SpringBootApplication
public class RatesApplication {
    static void main(String[] args) {
        SpringApplication.run(RatesApplication.class, args);
    }
}
