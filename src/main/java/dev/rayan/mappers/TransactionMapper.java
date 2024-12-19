package dev.rayan.mappers;

import dev.rayan.dto.respose.TransactionResponse;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.utils.FormatterUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "jakarta-cdi", imports = {FormatterUtils.class, BigDecimal.class})
public interface TransactionMapper {

    default String getDefaultMessage(){ return "Server unavailable"; }

    @Mapping(target = "currentValue", expression = "java( (bitcoin.getLast() == null) ? getDefaultMessage() : FormatterUtils.formatMoney(bitcoin.getLast()))")
    @Mapping(target = "valueDate", expression = "java( (bitcoin.getLast() == null) ? getDefaultMessage() : FormatterUtils.formatDate(bitcoin.getTime()))")


    @Mapping(target = "units", expression = "java(transaction.getQuantity().toString())")
    @Mapping(target = "transactionDate", expression = "java(FormatterUtils.formatDate(transaction.getCreatedAt()))")
    @Mapping(target = "type", expression = "java(transaction.getType().toString())")
    @Mapping(target = "total", expression = "java((bitcoin.getLast() == null) ? getDefaultMessage() : FormatterUtils.formatMoney(bitcoin.getLast().multiply(transaction.getQuantity())))")
    TransactionResponse transactionInfoToTransactionResponse(Transaction transaction, Bitcoin bitcoin);

}
