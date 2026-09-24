package ru.eremin.springbootprometheusgrafana.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


public record RateDto(
        LocalDate date,
        String charCode,
        Integer nominal,
        BigDecimal value,
        BigDecimal vunitRate
) {
}
