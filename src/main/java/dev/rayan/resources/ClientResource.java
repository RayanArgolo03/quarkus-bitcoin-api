package dev.rayan.resources;

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
import dev.rayan.report.ExcelReportAbstract;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.services.TransactionService;
import dev.rayan.utils.ConverterEnumUtils;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Context
    UriInfo uriInfo;

    @Inject
    Logger log;

    @Inject
    TransactionService service;

    @POST
    @Transactional
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Valid final TransactionRequest request) {

        //Todo valida se cliente está logado

        log.info("Persisting buy transaction");
        final Transaction transaction = service.persistTransaction(request, TransactionType.PURCHASE);

        log.info("Quoting bitcoin");
        final Bitcoin quote = service.quoteBitcoin();

        log.info("Creating resource URI");
        final URI uri = service.createUri(uriInfo, transaction.getId());

        return Response.created(uri)
                .entity(service.getMappedTransaction(transaction, quote))
                .build();
    }


    @POST
    @Transactional
    @Path("/sell-bitcoins")
    public Response sellBitcoins(@Valid final TransactionRequest request) {

        //Todo verifica se cliente está logado

        log.info("Find all transactions");
        final List<Transaction> transactions = service.findAllTransactions(request.client());

        log.info("Validate sale quantity");
        service.validateQuantity(transactions, request.quantity());

        log.info("Persisting sale transaction");
        final Transaction transaction = service.persistTransaction(request, TransactionType.SALE);

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = service.quoteBitcoin();

        log.info("Creating resource URI");
        final URI uri = service.createUri(uriInfo, transaction.getId());

        return Response.created(uri)
                .entity(service.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @Path("/wallet/summary")
    public Response findTransactionsSummaryByType(final Client client, @QueryParam("type") final List<String> types) {

        //Todo cliente precisa estar logado
        log.info("Mapping string types to enum");
        final List<TransactionType> transactionTypes = ConverterEnumUtils.convertEnums(TransactionType.class, types);

        log.info("Finding transactions by type");
        return Response.ok(service.findTransactionsSummaryByType(client, transactionTypes))
                .build();
    }

    @GET
    @Path("/wallet/report")
    public Response generateTransactionReport(final Client client,
                                              @QueryParam("format") @EnumValidator(enumClass = TransactionReportFormat.class) String format,
                                              @QueryParam("period") @EnumValidator(enumClass = TransactionReportPeriod.class) String period)
            throws IllegalAccessException, IOException {

        //Todo cliente precisa estar logado

        log.info("Mapping string period to enum");
        final TransactionReportPeriod transactionPeriod = ConverterEnumUtils.convertEnum(TransactionReportPeriod.class, period);

        log.infof("Finding transaction report on %s", transactionPeriod);
        final TransactionReportResponse response = service.findTransactionReport(client, transactionPeriod);

        //quoteBitcoin throws ApiException, if this occurred the bitcoin attributes will become unavailable
        try {
            log.info("Quoting bitcoin");
            final Bitcoin bitcoin = service.quoteBitcoin();

            log.info("Sucess! Setting bitcoin attributes in response");
            service.setBitcoinAttributesInResponse(response, bitcoin);

        } catch (ApiException e) {
            log.infof("%s! Setting null bitcoin attributes in response", e.getMessage());
            service.setBitcoinAttributesInResponse(response, null);
        }

        log.info("Mapping string report format to enum");
        final TransactionReportFormat transactionFormat = ConverterEnumUtils.convertEnum(TransactionReportFormat.class, format);

        //Todo
        log.infof("Generating report in format %s", transactionFormat);
        final ReportAbstractFile reportAbstractFile = ReportFileFactory.createReportAbstractFile(transactionFormat);
        reportAbstractFile.generateReport(response, transactionPeriod);

        return Response.ok("Report created and downloaded!")
                .build();
    }

    @GET
    @Path("/testing")
    public Response testing()
            throws IllegalAccessException, IOException {

        ReportFileFactory.createReportAbstractFile(TransactionReportFormat.EXCEL)
                .generateReport(new TransactionReportResponse(
                        "a", "a", "a", "a", "a", "a", "a", "a"
                ), TransactionReportPeriod.CURRENT_YEAR);
        return Response.ok("Report generated and downloaded!")
                .build();
    }

}
