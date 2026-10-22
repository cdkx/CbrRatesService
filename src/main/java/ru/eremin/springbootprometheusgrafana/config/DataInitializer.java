package ru.eremin.springbootprometheusgrafana.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cbr.scheduler.zone:Europe/Moscow}")
    private String zoneIdStr;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting initial data fetch from CBR on application startup...");
        try {
            ratesService.fetchAndSaveCurrencies();

            LocalDate today = LocalDate.now(ZoneId.of(zoneIdStr));
            ratesService.fetchAndSaveDailyRates(today);

            log.info("Initial data fetch completed successfully. Database is ready.");
        } catch (Exception e) {
            log.error("Failed to fetch initial data from CBR. Application will start, but DB might be empty.", e);
        }
    }
}
