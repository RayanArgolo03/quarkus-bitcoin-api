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

    @Mapping(target = "currentValue", expression = "java(FormatterUtils.formatMoney(bitcoin.getLast()))")
    @Mapping(target = "valueDate", expression = "java(FormatterUtils.formatDate(bitcoin.getTime()))")


    @Mapping(target = "units", expression = "java(transaction.getQuantity().toString())")
    @Mapping(target = "transactionDate", expression = "java(FormatterUtils.formatDate(transaction.getCreatedAt()))")
    @Mapping(target = "type", expression = "java(transaction.getType().toString())")
    @Mapping(target = "total", expression = "java(FormatterUtils.formatMoney(bitcoin.getLast().multiply(transaction.getQuantity())))")
    TransactionResponse transactionInfoToTransactionResponse(Transaction transaction, Bitcoin bitcoin);

}
