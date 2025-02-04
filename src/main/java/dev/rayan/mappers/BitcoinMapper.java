package dev.rayan.mappers;

import dev.rayan.dto.response.bitcoin.BitcoinResponse;
import dev.rayan.model.bitcoin.Bitcoin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface BitcoinMapper {

    @Mapping(target = "price", source = "last", numberFormat = "$#.00")
    @Mapping(target = "date", source = "time", dateFormat = "dd/MM/yyyy HH:mm")
    BitcoinResponse bitcoinToBitcoinQuoteResponse(Bitcoin bitcoin);
}
