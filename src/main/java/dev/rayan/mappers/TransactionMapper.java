package dev.rayan.mappers;

import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.utils.NumberFormatUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = JAKARTA_CDI, imports = NumberFormatUtils.class, unmappedTargetPolicy = ERROR)
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "client", source = "client")
    @Mapping(target = "type", source = "type")
    Transaction requestToTransaction(TransactionRequest request, Client client, TransactionType type);

    @Mapping(target = "id", source = "transaction.id")
    @Mapping(target = "bitcoinCurrentValue", expression = "java(bitcoin.getPriceFormatted())")
    @Mapping(target = "currentValueDate", expression = "java(bitcoin.quotedAt().toLocalDate())")
    @Mapping(target = "quantity", expression = "java(transaction.getQuantity() + \" unit(s)\")")
    @Mapping(target = "createdAt", source = "transaction.createdAt")
    @Mapping(target = "type", expression = "java(transaction.getType().getValue())")

    @Mapping(target = "transactionTotal", expression = """
                    java(
                        NumberFormatUtils.formatNumber(
                            transaction.getQuantity().multiply(bitcoin.price())
                        )
                    )
            """)
    TransactionResponse transactionToResponse(Transaction transaction, BitcoinResponse bitcoin);

}
