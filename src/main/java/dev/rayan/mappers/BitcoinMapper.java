package dev.rayan.mappers;

import dev.rayan.dto.respose.BitcoinQuotedResponseDTO;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.utils.FormatterUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@Mapper(componentModel = "jakarta-cdi", imports = {FormatterUtils.class})
public interface BitcoinMapper {

    @Mapping(target = "price", expression = "java(FormatterUtils.formatMoney(bitcoin.getLast()))")
    @Mapping(target = "date", expression = "java(FormatterUtils.formatDate(bitcoin.getTime()))")
    BitcoinQuotedResponseDTO bitcoinToBitcoinQuoteResponse(Bitcoin bitcoin);

}
