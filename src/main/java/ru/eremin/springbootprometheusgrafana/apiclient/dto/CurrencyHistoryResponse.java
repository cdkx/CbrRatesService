package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


@JsonRootName("ValCurs")
public record CurrencyHistoryResponse(
        @JacksonXmlProperty(isAttribute = true, localName = "ID")
        String id,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Record")
        List<HistoricalRate> history) {
}
