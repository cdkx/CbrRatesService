package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import lombok.Data;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;


@Data
public class Record {
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    private String date;

    @JacksonXmlProperty(isAttribute = true, localName = "Id")
    private String valuteId;

    @JacksonXmlProperty(localName = "Nominal")
    private String nominal;

    @JacksonXmlProperty(localName = "Value")
    private String value;
}
