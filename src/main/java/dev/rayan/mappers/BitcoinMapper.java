package dev.rayan.mappers;

import dev.rayan.dto.respose.BitcoinQuoteResponse;
import dev.rayan.model.bitcoin.Bitcoin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@Mapper(componentModel = "jakarta-cdi", imports = {ResolverStyle.class, DateTimeFormatter.class, NumberFormat.class})
public interface BitcoinMapper {

    @Mapping(target = "price", expression = "java(NumberFormat.getCurrencyInstance().format(bitcoin.getLast()))")
    @Mapping(target = "date", expression = "java(DateTimeFormatter.ofPattern(\"dd/MM/uuuu HH:mm\").withResolverStyle(ResolverStyle.STRICT).format(bitcoin.getTime()))")
    BitcoinQuoteResponse bitcoinToBitcoinQuoteResponse(Bitcoin bitcoin);

}
