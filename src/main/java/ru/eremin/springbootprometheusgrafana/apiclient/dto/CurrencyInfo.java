package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public record CurrencyInfo(
        @JacksonXmlProperty(isAttribute = true, localName = "ID")
        String id,

        @JacksonXmlProperty(localName = "Name")
        String name,

        @JacksonXmlProperty(localName = "EngName")
        String engName,

        @JacksonXmlProperty(localName = "Nominal")
        String nominal,

        @JacksonXmlProperty(localName = "ParentCode")
        String parentCode,

        @JacksonXmlProperty(localName = "ISO_Num_Code")
        String isoNumCode,

        @JacksonXmlProperty(localName = "ISO_Char_Code")
        String isoCharCode
) {
}
