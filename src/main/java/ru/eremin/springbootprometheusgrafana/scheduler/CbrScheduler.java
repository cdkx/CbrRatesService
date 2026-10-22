package ru.eremin.springbootprometheusgrafana.scheduler;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.eremin.springbootprometheusgrafana.service.RatesService;

import java.time.LocalDate;
import java.time.ZoneId;


@Slf4j
@Component
@RequiredArgsConstructor
public class CbrScheduler {
    private final RatesService ratesService;

    @Timed("schedulerCbr")
    @Scheduled(cron = "${cbr.scheduler.cron}", zone = "${cbr.scheduler.zone}")
    public void fetchDailyRates() {
        log.info("Starting scheduled CBR rates fetch");
        try {
            LocalDate today = LocalDate.now(ZoneId.of("Europe/Moscow"));
            ratesService.fetchAndSaveDailyRates(today);
            log.info("Finished scheduled CBR rates fetch successfully");
        } catch (Exception e) {
            log.error("Error fetching CBR rates", e);
        }
    }
}
