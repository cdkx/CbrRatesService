package ru.eremin.springbootprometheusgrafana.service;


import ru.eremin.springbootprometheusgrafana.model.Currency;
import ru.eremin.springbootprometheusgrafana.model.dto.RateDto;

import java.time.LocalDate;
import java.util.List;


public interface RatesService {

    /**
     * Получает актуальные курсы всех валют на заданную дату с ЦБ РФ
     * и сохраняет их в базу данных.
     */
    void fetchAndSaveDailyRates(LocalDate date);

    /**
     * Возвращает динамику курса валюты за указанный период.
     * Если данных нет в БД, выполняет fallback-запрос к API ЦБ РФ.
     */
    List<RateDto> getRates(String charCode, LocalDate dateFrom, LocalDate dateTo);

    /**
     * Получает историческую динамику конкретной валюты за период
     * и сохраняет недостающие записи в БД.
     */
    void fetchAndSaveDynamicRates(Currency currency, LocalDate dateFrom, LocalDate dateTo);


    void fetchAndSaveCurrencies();
}
