package ru.eremin.springbootprometheusgrafana.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.eremin.springbootprometheusgrafana.apiclient.CbrFeignClient;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DailyValCurs;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DynamicValCurs;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.ItemDto;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.Record;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.ValutaDto;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.Valute;
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
        DailyValCurs dailyValCurs = cbrClient.getDailyRates(dateReq);

        if (dailyValCurs == null || dailyValCurs.getValutes() == null) {
            log.warn("No data received from CBR for date {}", date);
            return;
        }

        LocalDate actualDate = LocalDate.parse(dailyValCurs.getDate(), CBR_DATE_FORMATTER);

        Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCbrId, Function.identity()));

        Set<Long> existingRateCurrencyIds = exchangeRateRepository.findExistingCurrencyIdsByDate(actualDate);

        List<Currency> newCurrencies = new ArrayList<>();
        List<ExchangeRate> ratesToSave = new ArrayList<>();

        for (Valute valute : dailyValCurs.getValutes()) {
            Currency currency = currencyMap.get(valute.getId());

            if (currency == null) {
                currency = new Currency();
                currency.setCbrId(valute.getId());
                currency.setCharCode(valute.getCharCode());
                currency.setNumCode(valute.getNumCode());
                currency.setName(valute.getName());
                newCurrencies.add(currency);
                currencyMap.put(currency.getCbrId(), currency);
            }

            if (currency.getId() != null && !existingRateCurrencyIds.contains(currency.getId())) {
                ExchangeRate rate = new ExchangeRate();
                rate.setCurrency(currency);
                rate.setRateDate(actualDate);
                rate.setNominal(Integer.parseInt(valute.getNominal()));
                rate.setValue(parseValue(valute.getValue()));
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
        DynamicValCurs dynamic = cbrClient.getDynamicRates(
                dateFrom.format(CBR_REQ_DATE_FORMATTER),
                dateTo.format(CBR_REQ_DATE_FORMATTER),
                currency.getCbrId()
        );

        if (dynamic == null || dynamic.getRecords() == null) {
            return;
        }

        Set<LocalDate> existingDates = exchangeRateRepository.findExistingDatesByCurrencyAndPeriod(currency, dateFrom, dateTo);

        List<ExchangeRate> ratesToSave = new ArrayList<>();

        for (Record record : dynamic.getRecords()) {
            LocalDate recordDate = LocalDate.parse(record.getDate(), CBR_DATE_FORMATTER);

            if (!existingDates.contains(recordDate)) {
                ExchangeRate rate = new ExchangeRate();
                rate.setCurrency(currency);
                rate.setRateDate(recordDate);
                rate.setNominal(Integer.parseInt(record.getNominal()));
                rate.setValue(parseValue(record.getValue()));
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
        ValutaDto valuta = cbrClient.getCurrencies();
        if (valuta == null || valuta.getItems() == null) {
            log.warn("No currencies data received from CBR");
            return;
        }

        Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCbrId, Function.identity()));

        List<Currency> currenciesToSave = new ArrayList<>();

        for (ItemDto item : valuta.getItems()) {
            Currency currency = currencyMap.get(item.getId());

            if (currency == null) {
                currency = new Currency();
                currency.setCbrId(item.getId());
            }

            currency.setName(item.getName());
            currency.setCharCode(item.getIsoCharCode());
            currency.setNumCode(item.getIsoNumCode());

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
