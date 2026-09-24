package ru.eremin.springbootprometheusgrafana.apiclient;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DailyRatesResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyHistoryResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyCatalogResponse;


@Timed("gettingRatesFromCbr")
@FeignClient(name = "cbrClient", url = "${cbr.base-url}")
public interface CbrFeignClient {

    @GetMapping("/scripts/XML_daily.asp")
    DailyRatesResponse getDailyRates(@RequestParam("date_req") String dateReq);

    @GetMapping("/scripts/XML_dynamic.asp")
    CurrencyHistoryResponse getDynamicRates(@RequestParam("date_req1") String dateReq1,
                                            @RequestParam("date_req2") String dateReq2,
                                            @RequestParam("VAL_NM_RQ") String valNmRq);

    @GetMapping("/scripts/XML_valFull.asp")
    CurrencyCatalogResponse getCurrencies();
}
