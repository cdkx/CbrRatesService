package ru.eremin.springbootprometheusgrafana.apiclient;

import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyCatalogResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.CurrencyHistoryResponse;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DailyRatesResponse;


@Timed("gettingRatesFromCbr")
@HttpExchange
public interface CbrClient {

    @GetExchange("/scripts/XML_daily.asp")
    DailyRatesResponse getDailyRates(@RequestParam("date_req") String dateReq);

    @GetExchange("/scripts/XML_dynamic.asp")
    CurrencyHistoryResponse getDynamicRates(@RequestParam("date_req1") String dateReq1,
                                            @RequestParam("date_req2") String dateReq2,
                                            @RequestParam("VAL_NM_RQ") String valNmRq);

    @GetExchange("/scripts/XML_valFull.asp")
    CurrencyCatalogResponse getCurrencies();
}
