package ru.eremin.springbootprometheusgrafana.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.eremin.springbootprometheusgrafana.apiclient.CbrFeignClient;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyCatalogResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyHistoryResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyInfo;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DailyRate;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DailyRatesResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.HistoricalRate;
import ru.eremin.springbootprometheusgrafana.mapper.RateMapper;
import ru.eremin.springbootprometheusgrafana.model.Currency;
import ru.eremin.springbootprometheusgrafana.model.ExchangeRate;
import ru.eremin.springbootprometheusgrafana.model.dto.RateDto;
import ru.eremin.springbootprometheusgrafana.repository.CurrencyRepository;
import ru.eremin.springbootprometheusgrafana.repository.ExchangeRateRepository;
import ru.eremin.springbootprometheusgrafana.service.RatesService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class RatesServiceImpl implements RatesService {
    private final CbrFeignClient cbrClient;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final RateMapper rateMapper;

    private static final DateTimeFormatter CBR_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter CBR_REQ_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Transactional
    public void fetchAndSaveDailyRates(LocalDate date) {
        String dateReq = date.format(CBR_REQ_DATE_FORMATTER);
        DailyRatesResponse dailyValCurs = cbrClient.getDailyRates(dateReq);

        if (dailyValCurs == null || dailyValCurs.valutes() == null) {
            log.warn("No data received from CBR for date {}", date);
            return;
        }

        LocalDate actualDate = LocalDate.parse(dailyValCurs.date(), CBR_DATE_FORMATTER);

        Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCbrId, Function.identity()));

        Set<Long> existingRateCurrencyIds = exchangeRateRepository.findExistingCurrencyIdsByDate(actualDate);

        List<Currency> newCurrencies = new ArrayList<>();
        List<ExchangeRate> ratesToSave = new ArrayList<>();

        for (DailyRate valute : dailyValCurs.valutes()) {
            Currency currency = currencyMap.get(valute.id());

            if (currency == null) {
                currency = new Currency();
                currency.setCbrId(valute.id());
                currency.setCharCode(valute.charCode());
                currency.setNumCode(valute.numCode());
                currency.setName(valute.name());
                newCurrencies.add(currency);
                currencyMap.put(currency.getCbrId(), currency);
            }

            if (currency.getId() != null && !existingRateCurrencyIds.contains(currency.getId())) {
                ExchangeRate rate = new ExchangeRate();
                rate.setCurrency(currency);
                rate.setRateDate(actualDate);
                rate.setNominal(Integer.parseInt(valute.nominal()));
                rate.setValue(parseValue(valute.value()));
                ratesToSave.add(rate);
            }
        }

        if (!newCurrencies.isEmpty()) {
            currencyRepository.saveAll(newCurrencies);
            log.info("Saved {} new currencies", newCurrencies.size());
        }

        if (!ratesToSave.isEmpty()) {
            long startTime = System.currentTimeMillis();

            exchangeRateRepository.saveAll(ratesToSave);

            long endTime = System.currentTimeMillis();
            log.info("Saved {} exchange rates for date {} in {} ms.", ratesToSave.size(), actualDate, (endTime - startTime));
        }
    }

    @Override
    public List<RateDto> getRates(String charCode, LocalDate dateFrom, LocalDate dateTo) {
        Currency currency = currencyRepository.findByCharCode(charCode.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found"));

        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyAndDateRangeWithCurrency(currency, dateFrom, dateTo);

        if (rates.isEmpty()) {
            try {
                fetchAndSaveDynamicRates(currency, dateFrom, dateTo);
                rates = exchangeRateRepository.findByCurrencyAndDateRangeWithCurrency(currency, dateFrom, dateTo);
            } catch (Exception e) {
                log.error("Fallback Fetch failed", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cbr service is unavailable");
            }
        }

        return rateMapper.toDtoList(rates);
    }

    @Override
    @Transactional
    public void fetchAndSaveDynamicRates(Currency currency, LocalDate dateFrom, LocalDate dateTo) {
        CurrencyHistoryResponse dynamic = cbrClient.getDynamicRates(
                dateFrom.format(CBR_REQ_DATE_FORMATTER),
                dateTo.format(CBR_REQ_DATE_FORMATTER),
                currency.getCbrId()
        );

        if (dynamic == null || dynamic.history() == null) {
            return;
        }

        Set<LocalDate> existingDates = exchangeRateRepository.findExistingDatesByCurrencyAndPeriod(currency, dateFrom, dateTo);

        List<ExchangeRate> ratesToSave = new ArrayList<>();

        for (HistoricalRate record : dynamic.history()) {
            LocalDate recordDate = LocalDate.parse(record.date(), CBR_DATE_FORMATTER);

            if (!existingDates.contains(recordDate)) {
                ExchangeRate rate = new ExchangeRate();
                rate.setCurrency(currency);
                rate.setRateDate(recordDate);
                rate.setNominal(Integer.parseInt(record.nominal()));
                rate.setValue(parseValue(record.value()));
                ratesToSave.add(rate);
            }
        }

        if (!ratesToSave.isEmpty()) {
            exchangeRateRepository.saveAll(ratesToSave);
            log.info("Saved {} dynamic rates for currency {}", ratesToSave.size(), currency.getCharCode());
        }
    }

    @Override
    @Transactional
    public void fetchAndSaveCurrencies() {
        CurrencyCatalogResponse valuta = cbrClient.getCurrencies();
        if (valuta == null || valuta.items() == null) {
            log.warn("No currencies data received from CBR");
            return;
        }

        Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCbrId, Function.identity()));

        List<Currency> currenciesToSave = new ArrayList<>();

        for (CurrencyInfo item : valuta.items()) {
            Currency currency = currencyMap.get(item.id());

            if (currency == null) {
                currency = new Currency();
                currency.setCbrId(item.id());
            }

            currency.setName(item.name());
            currency.setCharCode(item.isoCharCode());
            currency.setNumCode(item.isoNumCode());

            currenciesToSave.add(currency);
        }

        if (!currenciesToSave.isEmpty()) {
            currencyRepository.saveAll(currenciesToSave);
            log.info("Saved/updated {} currencies", currenciesToSave.size());
        }
    }

    private BigDecimal parseValue(String value) {
        if (value == null || value.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(value.replace(",", "."));
    }
}
