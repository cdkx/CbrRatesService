package ru.eremin.springbootprometheusgrafana.apiclient;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DailyValCurs;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.DynamicValCurs;
import ru.eremin.springbootprometheusgrafana.apiclient.dto.ValutaDto;


@Timed("gettingRatesFromCbr")
@FeignClient(name = "cbrClient", url = "${cbr.base-url}")
public interface CbrFeignClient {

    @GetMapping("/scripts/XML_daily.asp")
    DailyValCurs getDailyRates(@RequestParam("date_req") String dateReq);

    @GetMapping("/scripts/XML_dynamic.asp")
    DynamicValCurs getDynamicRates(@RequestParam("date_req1") String dateReq1,
                                   @RequestParam("date_req2") String dateReq2,
                                   @RequestParam("VAL_NM_RQ") String valNmRq);

    @GetMapping("/scripts/XML_valFull.asp")
    ValutaDto getCurrencies();
}
