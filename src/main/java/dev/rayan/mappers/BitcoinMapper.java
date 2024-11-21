package dev.rayan.mappers;

import dev.rayan.dto.respose.BitcoinQuotedResponse;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.utils.FormatterUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi", imports = {FormatterUtils.class})
public interface BitcoinMapper {

    @Mapping(target = "price", expression = "java(FormatterUtils.formatMoney(bitcoin.getLast()))")
    @Mapping(target = "date", expression = "java(FormatterUtils.formatDate(bitcoin.getTime()))")
    BitcoinQuotedResponse bitcoinToBitcoinQuoteResponse(Bitcoin bitcoin);

}
