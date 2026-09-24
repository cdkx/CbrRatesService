package ru.eremin.springbootprometheusgrafana.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.eremin.springbootprometheusgrafana.model.ExchangeRate;
import ru.eremin.springbootprometheusgrafana.model.dto.RateDto;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface RateMapper {
    @Mapping(source = "rateDate", target = "date")
    @Mapping(source = "currency.charCode", target = "charCode")
    RateDto toDto(ExchangeRate rate);

    List<RateDto> toDtoList(List<ExchangeRate> rates);
}
