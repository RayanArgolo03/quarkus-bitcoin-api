package dev.rayan.mappers;

import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface TransactionMapper {


    //    @Mapping(target = "currentValue", expression = "java( (bitcoin == null) ? getDefaultMessage() : FormatterUtils.formatMoney(bitcoin.getLast()))")
//    @Mapping(target = "valueDate", expression = "java( (bitcoin == null) ? getDefaultMessage() : FormatterUtils.formatDate(bitcoin.getTime()))")
//
//
//    @Mapping(target = "units", expression = "java(transaction.getQuantity().toString())")
//    @Mapping(target = "transactionDate", expression = "java(FormatterUtils.formatDate(transaction.getCreatedAt()))")
//    @Mapping(target = "type", expression = "java(transaction.getType().toString())")
//    @Mapping(target = "total", expression = "java( (bitcoin == null) ? getDefaultMessage() : FormatterUtils.formatMoney(bitcoin.getLast().multiply(transaction.getQuantity())))")
    TransactionResponse transactionInfoToTransactionResponse(Transaction transaction, Bitcoin bitcoin);

}
