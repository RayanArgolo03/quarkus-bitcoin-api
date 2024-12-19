package dev.rayan.resources;

import dev.rayan.dto.request.TransactionFiltersRequest;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.enums.validation.EnumValidator;
import dev.rayan.exceptions.ApiException;
import dev.rayan.factory.ReportFileFactory;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.services.TransactionService;
import dev.rayan.utils.ConverterEnumUtils;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Context
    UriInfo uriInfo;

    @Inject
    Logger log;

    @Inject
    TransactionService transactionService;

    @POST
    @Transactional
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Valid final TransactionRequest request) {

        //Todo valida se cliente está logado

        log.info("Persisting buy transaction");
        final Transaction transaction = transactionService.persistTransaction(request, TransactionType.PURCHASE);

        log.info("Quoting bitcoin");
        final Bitcoin quote = transactionService.quoteBitcoin();

        log.info("Creating resource URI");
        final URI uri = transactionService.createUri(uriInfo, transaction.getId());

        return Response.created(uri)
                .entity(transactionService.getMappedTransaction(transaction, quote))
                .build();
    }


    @POST
    @Transactional
    @Path("/sell-bitcoins")
    public Response sellBitcoins(@Valid final TransactionRequest request) {

        //Todo verifica se cliente está logado

        log.info("Find all transactions");
        final List<Transaction> transactions = transactionService.findAllTransactions(request.client());

        log.info("Validate sale quantity");
        transactionService.validateQuantity(transactions, request.quantity());

        log.info("Persisting sale transaction");
        final Transaction transaction = transactionService.persistTransaction(request, TransactionType.SALE);

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        log.info("Creating resource URI");
        final URI uri = transactionService.createUri(uriInfo, transaction.getId());

        return Response.created(uri)
                .entity(transactionService.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @Path("/transactions/summary-by-types")
    public Response findTransactionsSummaryByTypes(final Client client, @QueryParam("type") final List<String> types) {

        //Todo cliente precisa estar logado

        log.info("Mapping string types to enum");
        final List<TransactionType> transactionTypes = ConverterEnumUtils.convertEnums(TransactionType.class, types);

        log.info("Finding transactions by type");
        return Response.ok(transactionService.findTransactionsSummaryByType(client, transactionTypes))
                .build();
    }

    @GET
    @Path("/transactions/summary-by-filters")
    public Response findTransactionsSummaryByFilters(final Client client, @BeanParam @Valid final TransactionFiltersRequest request) {
        //Todo cliente precisa estar logado

        log.info("Finding transactions summary by filters");
        return Response.ok(transactionService.findTransactionSummaryByFilters(client, request))
                .build();
    }

    @GET
    @Path("/transactions/{id}")
    public Response findTransactionById(@PathParam("id") final UUID id) {

        //Todo client precisa estar logado

        log.info("Finding transaction by id");
        final Transaction transaction = transactionService.findTransactionById(id);

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        return Response.ok(transactionService.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @Path("/transactions/last-transaction-by-quantity")
    public Response findTransactionByQuantity(final Client client,
                                              @QueryParam("quantity")
                                              @DecimalMin(value = "0", inclusive = false, message = "Quantity must be greater than 0!!")
                                              @NotNull(message = "Quantity required!") final BigDecimal quantity,
                                              @QueryParam("sortCreatedAt")
                                              @DefaultValue("Ascending") final Sort.Direction sortCreatedAtDirection) {

        //Todo client precisa estar logado

        log.infof("Finding transaction by quantity and sort created at %s", sortCreatedAtDirection);
        final Transaction transaction = transactionService.findTransactionByQuantity(client, quantity, sortCreatedAtDirection);

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        return Response.ok(transactionService.getMappedTransaction(transaction, bitcoin))
                .build();
    }


    @GET
    @Path("/transactions/report")
    public Response createTransactionReport(final Client client,
                                            @QueryParam("format") @EnumValidator(enumClass = TransactionReportFormat.class) String format,
                                            @QueryParam("period") @EnumValidator(enumClass = TransactionReportPeriod.class) String period) {

        //Todo cliente precisa estar logado

        log.info("Mapping string period to enum");
        final TransactionReportPeriod reportPeriod = ConverterEnumUtils.convertEnum(TransactionReportPeriod.class, period);

        log.infof("Finding transaction report on %s", reportPeriod);
        final TransactionReportResponse reportResponse = transactionService.findTransactionReport(client, reportPeriod);

        //quoteBitcoin throws ApiException, if this occurred the bitcoin attributes will become unavailable
        try {
            log.info("Quoting bitcoin");
            final Bitcoin bitcoin = transactionService.quoteBitcoin();

            log.info("Setting bitcoin attributes in reportResponse");
            transactionService.setBitcoinAttributesInResponse(reportResponse, bitcoin);

        } catch (ApiException e) {
            log.infof("%s Setting null bitcoin attributes in reportResponse", e.getMessage());
            transactionService.setBitcoinAttributesInResponse(reportResponse, null);
        }

        log.info("Mapping string report format to enum");
        final TransactionReportFormat reportFormat = ConverterEnumUtils.convertEnum(TransactionReportFormat.class, format);

        try {
            log.infof("Generating report in format %s", reportFormat);
            final ReportAbstractFile reportAbstractFile = ReportFileFactory.createReportAbstractFile(reportFormat);
            reportAbstractFile.createReport(reportResponse, reportPeriod);

            return Response.ok("Report created and downloaded!")
                    .build();

        } catch (IllegalAccessException | IOException  e) {
            return Response.status(INTERNAL_SERVER_ERROR)
                    .entity("Server error! Contact the support in Linkedin: rayan_argolo")
                    .build();
        }
    }

}
