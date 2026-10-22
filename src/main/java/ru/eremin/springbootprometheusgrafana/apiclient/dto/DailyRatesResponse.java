package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


@JsonRootName("ValCurs")
public record DailyRatesResponse(
        @JacksonXmlProperty(isAttribute = true, localName = "Date")
        String date,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Valute")
        List<DailyRate> rates

) {
}

