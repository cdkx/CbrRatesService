package ru.eremin.springbootprometheusgrafana.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.eremin.springbootprometheusgrafana.service.RatesService;

import java.time.LocalDate;
import java.time.ZoneId;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final RatesService ratesService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting initial data fetch from CBR on application startup...");
        try {
            // 1. Загружаем базовый справочник валют (XML_val.asp)
            ratesService.fetchAndSaveCurrencies();

            // 2. Загружаем курсы за сегодня (XML_daily.asp). 
            // Это критически важно, так как именно здесь присутствуют CharCode (USD, EUR) и NumCode,
            // которых нет в базовом справочнике XML_val.asp.
            LocalDate today = LocalDate.now(ZoneId.of("${cbr.scheduler.zone}"));
            ratesService.fetchAndSaveDailyRates(today);

            log.info("Initial data fetch completed successfully. Database is ready.");
        } catch (Exception e) {
            log.error("Failed to fetch initial data from CBR. Application will start, but DB might be empty.", e);
        }
    }
}
