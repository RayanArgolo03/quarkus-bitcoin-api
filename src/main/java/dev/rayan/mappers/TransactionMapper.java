package dev.rayan.mappers;

import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.bitcoin.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.utils.FormatterUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Mapper(componentModel = "jakarta-cdi", imports = FormatterUtils.class)
public interface TransactionMapper {
    @Mapping(target = "client", source = "client")
    @Mapping(target = "id", ignore = true)
    Transaction requestToTransaction(TransactionRequest request, Client client, TransactionType type);

    @Mapping(target = "bitcoinCurrentValue", expression = "java(FormatterUtils.formatMoney(bitcoin.price()))")
    @Mapping(target = "currentValueDate", source = "bitcoin.quotedAt")
    @Mapping(target = "quantity", expression = "java(transaction.getQuantity() + \" units\")")
    @Mapping(target = "type", expression = "java(transaction.getType().getValue())")
    @Mapping(target = "transactionTotal", expression = "java(FormatterUtils.formatMoney(transaction.getQuantity().multiply(bitcoin.price())))")
    TransactionResponse transactionToResponse(Transaction transaction, BitcoinResponse bitcoin);

}
