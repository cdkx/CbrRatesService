package ru.eremin.springbootprometheusgrafana.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eremin.springbootprometheusgrafana.model.Currency;
import ru.eremin.springbootprometheusgrafana.model.ExchangeRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    boolean existsByCurrencyAndRateDate(Currency currency, LocalDate rateDate);

    List<ExchangeRate> findByCurrencyAndRateDateBetweenOrderByRateDateAsc(Currency currency, LocalDate from, LocalDate to);

    @Query("""
            SELECT er.currency.id FROM ExchangeRate er
            WHERE er.rateDate = :date
            """)
    Set<Long> findExistingCurrencyIdsByDate(@Param("date") LocalDate date);

    @Query("""
            SELECT er.rateDate FROM ExchangeRate er
            WHERE er.currency = :currency AND er.rateDate BETWEEN :from AND :to
            """)
    Set<LocalDate> findExistingDatesByCurrencyAndPeriod(
            @Param("currency") Currency currency,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
            SELECT er FROM ExchangeRate er
            JOIN FETCH er.currency
            WHERE er.currency = :currency
            AND er.rateDate BETWEEN :from AND :to
            ORDER BY er.rateDate
            """)
    List<ExchangeRate> findByCurrencyAndDateRangeWithCurrency(
            @Param("currency") Currency currency,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
