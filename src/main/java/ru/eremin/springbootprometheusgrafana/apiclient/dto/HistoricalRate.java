package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public record HistoricalRate(
        @JacksonXmlProperty(isAttribute = true, localName = "Date")
        String date,

        @JacksonXmlProperty(isAttribute = true, localName = "Id")
        String valuteId,

        @JacksonXmlProperty(localName = "Nominal")
        String nominal,

        @JacksonXmlProperty(localName = "Value")
        String value
) {
}
