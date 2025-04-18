package dev.rayan.mappers;

import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.text.NumberFormat;

import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = JAKARTA_CDI, unmappedTargetPolicy = ERROR)
public interface TransactionMapper {

    @Named("formatMoney")
    default String formatMoney(final BigDecimal value) {
        return NumberFormat.getCurrencyInstance()
                .format(value);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "client", source = "client")
    @Mapping(target = "type", source = "type")
    Transaction requestToTransaction(TransactionRequest request, Client client, TransactionType type);

    @Mapping(target = "bitcoinCurrentValue", source = "bitcoin.price", qualifiedByName = "formatMoney")
    @Mapping(target = "currentValueDate", source = "bitcoin.quotedAt")
    @Mapping(target = "quantity", expression = "java(transaction.getQuantity() + \" units\")")
    @Mapping(target = "type", expression = "java(transaction.getType().getValue())")
    @Mapping(target = "transactionTotal", expression = "java(formatMoney(transaction.getQuantity().multiply(bitcoin.price())))")
    TransactionResponse transactionToResponse(Transaction transaction, BitcoinResponse bitcoin);

}
