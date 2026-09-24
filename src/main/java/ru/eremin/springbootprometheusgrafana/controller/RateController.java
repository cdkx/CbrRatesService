package ru.eremin.springbootprometheusgrafana.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eremin.springbootprometheusgrafana.model.dto.RateDto;
import ru.eremin.springbootprometheusgrafana.service.RatesService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rates")
public class RateController {
    private final RatesService ratesService;

    @GetMapping
    public List<RateDto> getRates(
            @RequestParam String charCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        if (dateFrom == null) dateFrom = dateTo;
        return ratesService.getRates(charCode, dateFrom, dateTo);
    }
}
