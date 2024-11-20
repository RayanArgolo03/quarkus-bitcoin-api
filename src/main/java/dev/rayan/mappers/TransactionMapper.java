package dev.rayan.mappers;

import dev.rayan.dto.respose.TransactionResponseDTO;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.utils.FormatterUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "jakarta-cdi", imports = {FormatterUtils.class, BigDecimal.class})
public interface TransactionMapper {

    @Mapping(target = "currentValue", expression = """
            java( (bitcoin == null) ? "Bitcoin server unavailable": FormatterUtils.formatMoney(bitcoin.getLast()))
            """)

    @Mapping(target = "valueDate", expression = """
            java( (bitcoin == null) ? "Bitcoin server unavailable" : FormatterUtils.formatDate(bitcoin.getTime()))
            """)

    @Mapping(target = "unitsPurchased", expression = "java(String.format(\"%f\", transaction.getQuantity()))")
    @Mapping(target = "purchaseDate", expression = "java(FormatterUtils.formatDate(transaction.getCreatedAt()))")

    @Mapping(target = "total", expression = """
            java( (bitcoin == null)
                    ? "Bitcoin value unavailable"
                    : FormatterUtils.formatMoney(bitcoin.getLast().multiply(new BigDecimal(transaction.getQuantity())))
                 )
            """)
    TransactionResponseDTO transactionInfoToTransactionResponseDTO(Transaction transaction, Bitcoin bitcoin);
}
