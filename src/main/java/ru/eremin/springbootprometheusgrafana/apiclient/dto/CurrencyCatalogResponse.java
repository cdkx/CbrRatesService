package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


@JsonRootName("Valuta")
public record CurrencyCatalogResponse(
        @JacksonXmlProperty(isAttribute = true, localName = "name")
        String name,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Item")
        List<CurrencyInfo> items
) {
}
