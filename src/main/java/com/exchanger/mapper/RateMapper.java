package com.exchanger.mapper;

import com.exchanger.model.Rate;
import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RateMapper {

    @Mapping(target = "currency", expression = "java(com.exchanger.model.enums.Currency.valueOf(jsonNode.get(\"ccy\").asText()))")
    @Mapping(target = "buy", expression = "java(new java.math.BigDecimal(jsonNode.get(\"buy\").asText()))")
    @Mapping(target = "sale", expression = "java(new java.math.BigDecimal(jsonNode.get(\"sale\").asText()))")
    @Mapping(target = "receive", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    Rate fromJsonNodeToRate(JsonNode jsonNode);

    Rate updateRate(@MappingTarget Rate target, Rate source);

}
