package ru.eremin.springbootprometheusgrafana.apiclient.dto;

import lombok.Data;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;


@Data
public class ItemDto {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "EngName")
    private String engName;

    @JacksonXmlProperty(localName = "Nominal")
    private String nominal;

    @JacksonXmlProperty(localName = "ParentCode")
    private String parentCode;

    @JacksonXmlProperty(localName = "ISO_Num_Code")
    private String isoNumCode;

    @JacksonXmlProperty(localName = "ISO_Char_Code")
    private String isoCharCode;
}
