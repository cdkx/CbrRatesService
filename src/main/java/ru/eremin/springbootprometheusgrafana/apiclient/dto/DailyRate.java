package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public record DailyRate(
        @JacksonXmlProperty(isAttribute = true, localName = "ID")
        String id,

        @JacksonXmlProperty(localName = "NumCode")
        String numCode,

        @JacksonXmlProperty(localName = "CharCode")
        String charCode,

        @JacksonXmlProperty(localName = "Nominal")
        String nominal,

        @JacksonXmlProperty(localName = "Name")
        String name,

        @JacksonXmlProperty(localName = "Value")
        String value,

        @JacksonXmlProperty(localName = "VunitRate")
        String vunitRate
) {
}
